package reality;

public interface EventHandler {

    void handleEvent(QueueEvent evt);

    default String getName() {
        return getClass().getName();
    }

}
