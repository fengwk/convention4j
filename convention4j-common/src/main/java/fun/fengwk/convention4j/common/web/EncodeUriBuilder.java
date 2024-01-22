package fun.fengwk.convention4j.common.web;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengwk
 */
public class EncodeUriBuilder implements UriBuilder {

    private final UriBuilder delegate;

    public EncodeUriBuilder(UriBuilder delegate) {
        this.delegate = delegate;
    }

    public static EncodeUriBuilder fromUriString(String uri) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(uri);
        return new EncodeUriBuilder(uriComponentsBuilder);
    }

    public static EncodeUriBuilder fromUri(URI uri) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        return new EncodeUriBuilder(uriComponentsBuilder);
    }

    @Override
    public EncodeUriBuilder scheme(String scheme) {
        delegate.scheme(scheme);
        return this;
    }

    @Override
    public EncodeUriBuilder userInfo(String userInfo) {
        delegate.userInfo(userInfo);
        return this;
    }

    @Override
    public EncodeUriBuilder host(String host) {
        delegate.host(host);
        return this;
    }

    @Override
    public EncodeUriBuilder port(int port) {
        delegate.port(port);
        return this;
    }

    @Override
    public EncodeUriBuilder port(String port) {
        delegate.port(port);
        return this;
    }

    @Override
    public EncodeUriBuilder path(String path) {
        delegate.path(path);
        return this;
    }

    @Override
    public EncodeUriBuilder replacePath(String path) {
        delegate.replacePath(path);
        return this;
    }

    @Override
    public EncodeUriBuilder pathSegment(String... pathSegments) throws IllegalArgumentException {
        delegate.pathSegment(pathSegments);
        return this;
    }

    @Override
    public EncodeUriBuilder query(String query) {
        delegate.query(query);
        return this;
    }

    @Override
    public EncodeUriBuilder replaceQuery(String query) {
        delegate.replaceQuery(query);
        return this;
    }

    @Override
    public EncodeUriBuilder queryParam(String name, Object... values) {
        delegate.queryParam(name, values);
        return this;
    }

    @Override
    public EncodeUriBuilder queryParam(String name, Collection<?> values) {
        delegate.queryParam(name, values);
        return this;
    }

    @Override
    public EncodeUriBuilder queryParamIfPresent(String name, Optional<?> value) {
        delegate.queryParamIfPresent(name, value);
        return this;
    }

    @Override
    public EncodeUriBuilder queryParams(MultiValueMap<String, String> params) {
        delegate.queryParams(params);
        return this;
    }

    @Override
    public EncodeUriBuilder replaceQueryParam(String name, Object... values) {
        delegate.replaceQueryParam(name, values);
        return this;
    }

    @Override
    public EncodeUriBuilder replaceQueryParam(String name, Collection<?> values) {
        delegate.replaceQueryParam(name, values);
        return this;
    }

    @Override
    public EncodeUriBuilder replaceQueryParams(MultiValueMap<String, String> params) {
        delegate.replaceQueryParams(params);
        return this;
    }

    @Override
    public EncodeUriBuilder fragment(String fragment) {
        delegate.fragment(fragment);
        return this;
    }

    @Override
    public URI build(Object... uriVariables) {
        return delegate.build(uriVariables);
    }

    @Override
    public URI build(Map<String, ?> uriVariables) {
        return delegate.build(uriVariables);
    }

    public EncodeUriBuilder queryParamWithEncode(String name, Object... values) {
        delegate.queryParam(name, encodeValues(values));
        return this;
    }

    public EncodeUriBuilder queryParamWithEncode(String name, Collection<?> values) {
        delegate.queryParam(name, encodeValueCollection(values));
        return this;
    }

    public EncodeUriBuilder queryParamIfPresentWithEncode(String name, Optional<?> value) {
        Optional<?> encodedValue = value.map(this::encode);
        delegate.queryParamIfPresent(name, encodedValue);
        return this;
    }

    public EncodeUriBuilder queryParamsWithEncode(MultiValueMap<String, String> params) {
        delegate.queryParams(encodeParams(params));
        return this;
    }

    public EncodeUriBuilder replaceQueryParamWithEncode(String name, Object... values) {
        delegate.replaceQueryParam(name, encodeValues(values));
        return this;
    }

    public EncodeUriBuilder replaceQueryParamWithEncode(String name, Collection<?> values) {
        delegate.replaceQueryParam(name, encodeValueCollection(values));
        return this;
    }

    public EncodeUriBuilder replaceQueryParamsWithEncode(MultiValueMap<String, String> params) {
        delegate.replaceQueryParams(encodeParams(params));
        return this;
    }

    private Object[] encodeValues(Object... values) {
        Object[] encodedValues = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            encodedValues[i] = encode(values[i]);
        }
        return encodedValues;
    }

    private Collection<?> encodeValueCollection(Collection<?> values) {
        return values.stream().map(this::encode).collect(Collectors.toList());
    }

    private MultiValueMap<String, String> encodeParams(MultiValueMap<String, String> params) {
        LinkedMultiValueMap<String, String> encodedParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            String key = entry.getKey();
            List<String> encodedValues = new ArrayList<>();
            for (String value : entry.getValue()) {
                encodedValues.add(encode(value));
            }
            encodedParams.put(key, encodedValues);
        }
        return encodedParams;
    }

    private Object encode(Object obj) {
        if (obj == null) {
            return null;
        }
        String str = String.valueOf(obj);
        return encode(str);
    }

    private String encode(String str) {
        if (str == null) {
            return null;
        }
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

}
