package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketClientUtilsTest {

    private static final URI URI = java.net.URI.create("ws://localhost/test");

    @Test
    void shouldCancelHandshakeAndAbortConnectionOpenedAfterCancellation() {
        Fixture fixture = new Fixture();
        CompletableFuture<WebSocketConnection> connectionFuture = WebSocketClientUtils.connect(
            fixture.client, URI, new WebSocketListener() { });

        connectionFuture.cancel(true);

        // Cancelling the public future must also cancel the underlying HTTP handshake.
        verify(fixture.handshakeFuture).cancel(true);
        fixture.listenerCaptor.getValue().onOpen(fixture.webSocket);
        verify(fixture.webSocket).abort();
    }

    @Test
    void shouldNotifyListenerBeforeCompletingConnectionFuture() {
        Fixture fixture = new Fixture();
        AtomicReference<CompletableFuture<WebSocketConnection>> connectionFutureRef = new AtomicReference<>();
        AtomicBoolean futureDoneDuringCallback = new AtomicBoolean(true);
        CompletableFuture<WebSocketConnection> connectionFuture = WebSocketClientUtils.connect(
            fixture.client, URI, new WebSocketListener() {
                @Override
                public void onOpen(WebSocketConnection connection) {
                    futureDoneDuringCallback.set(connectionFutureRef.get().isDone());
                }
            });
        connectionFutureRef.set(connectionFuture);

        fixture.listenerCaptor.getValue().onOpen(fixture.webSocket);

        // Preserve the public callback-before-future ordering while still completing after onOpen returns.
        assertFalse(futureDoneDuringCallback.get());
        assertTrue(connectionFuture.isDone());
    }

    @Test
    void shouldAbortAndFailConnectionFutureWhenOpenListenerThrows() throws Exception {
        Fixture fixture = new Fixture();
        RuntimeException expected = new RuntimeException("listener failed");
        CompletableFuture<WebSocketConnection> connectionFuture = WebSocketClientUtils.connect(
            fixture.client, URI, new WebSocketListener() {
                @Override
                public void onOpen(WebSocketConnection connection) {
                    throw expected;
                }
            });

        RuntimeException callbackFailure = assertThrows(
            RuntimeException.class, () -> fixture.listenerCaptor.getValue().onOpen(fixture.webSocket));
        ExecutionException futureFailure = assertThrows(
            ExecutionException.class, () -> connectionFuture.get(1, TimeUnit.SECONDS));

        assertSame(expected, callbackFailure);
        assertSame(expected, futureFailure.getCause());
        verify(fixture.webSocket).abort();
    }

    @Test
    void shouldPropagateReactiveTextAndBinaryHandshakeFailures() throws Exception {
        for (boolean binary : new boolean[]{false, true}) {
            FailureFixture fixture = new FailureFixture();
            RuntimeException expected = new RuntimeException(binary ? "binary failed" : "text failed");
            AtomicReference<Throwable> actual = new AtomicReference<>();
            CountDownLatch errorReceived = new CountDownLatch(1);
            Disposable subscription = (binary
                ? ReactiveWebSocketClientUtils.connectBinary(fixture.client, URI)
                : ReactiveWebSocketClientUtils.connectText(fixture.client, URI))
                .subscribe(ignored -> { }, error -> {
                    actual.set(error);
                    errorReceived.countDown();
                });

            fixture.handshakeFuture.completeExceptionally(expected);

            assertTrue(errorReceived.await(1, TimeUnit.SECONDS));
            assertSame(expected, actual.get());
            assertTrue(subscription.isDisposed());
        }
    }

    @Test
    void shouldDisposePendingAndOpenReactiveConnections() {
        Fixture pending = new Fixture();
        Disposable pendingSubscription = ReactiveWebSocketClientUtils.connectText(pending.client, URI).subscribe();
        pendingSubscription.dispose();

        // Disposal before onOpen cancels the handshake rather than waiting for it to complete.
        verify(pending.handshakeFuture).cancel(true);
        pending.listenerCaptor.getValue().onOpen(pending.webSocket);
        verify(pending.webSocket).abort();

        Fixture open = new Fixture();
        Disposable openSubscription = ReactiveWebSocketClientUtils.connectText(open.client, URI).subscribe();
        open.listenerCaptor.getValue().onOpen(open.webSocket);
        openSubscription.dispose();

        // Disposal after onOpen sends a close frame to release the established connection.
        verify(open.webSocket, times(1)).sendClose(anyInt(), anyString());
    }

    private static class Fixture {
        private final HttpClient client = mock(HttpClient.class);
        private final WebSocket.Builder builder = mock(WebSocket.Builder.class);
        private final WebSocket webSocket = mock(WebSocket.class);
        @SuppressWarnings("unchecked")
        private final CompletableFuture<WebSocket> handshakeFuture = mock(CompletableFuture.class);
        private final org.mockito.ArgumentCaptor<WebSocket.Listener> listenerCaptor =
            org.mockito.ArgumentCaptor.forClass(WebSocket.Listener.class);

        private Fixture() {
            when(client.newWebSocketBuilder()).thenReturn(builder);
            when(builder.buildAsync(eq(URI), listenerCaptor.capture())).thenReturn(handshakeFuture);
            when(webSocket.isInputClosed()).thenReturn(false);
            when(webSocket.isOutputClosed()).thenReturn(false);
            when(webSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.completedFuture(webSocket));
        }
    }

    private static class FailureFixture {
        private final HttpClient client = mock(HttpClient.class);
        private final WebSocket.Builder builder = mock(WebSocket.Builder.class);
        private final CompletableFuture<WebSocket> handshakeFuture = new CompletableFuture<>();
        private final org.mockito.ArgumentCaptor<WebSocket.Listener> listenerCaptor =
            org.mockito.ArgumentCaptor.forClass(WebSocket.Listener.class);

        private FailureFixture() {
            when(client.newWebSocketBuilder()).thenReturn(builder);
            when(builder.buildAsync(eq(URI), listenerCaptor.capture())).thenReturn(handshakeFuture);
        }
    }
}
