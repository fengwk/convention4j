package fun.fengwk.convention4j.common.fs;

/**
 * @author fengwk
 */
public interface FileWatcherListener {

    /**
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_CREATE
     */
    default void onEntryCreate() {}

    /**
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_DELETE
     */
    default void onEntryDelete() {}

    /**
     * @see java.nio.file.StandardWatchEventKinds#ENTRY_MODIFY
     */
    default void onEntryModify() {}

}
