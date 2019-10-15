package org.liquidengine.legui.listener.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.event.Event;
import org.liquidengine.legui.event.WindowCloseEvent;
import org.liquidengine.legui.listener.EventListener;
import org.liquidengine.legui.system.LeguiSystem;
import org.liquidengine.legui.system.Window;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Default implementation of event processor.
 * <p>
 * Created by ShchAlexander on 1/25/2017.
 */
public class EventProcessorImpl implements EventProcessor {

    private static final Logger LOGGER = LogManager.getLogger();

    private Queue<Event> eventQueue = new ConcurrentLinkedQueue<>();


    /**
     * Should be called to process events.
     */
    @Override
    public void processEvents() {
        for (Event event = eventQueue.poll(); event != null; event = eventQueue.poll()) {
            Component targetComponent = event.getTargetComponent();
            if (targetComponent == null) {
                return;
            }
            List<? extends EventListener> listeners = targetComponent.getListenerMap().getListeners(event.getClass());
            for (EventListener listener : listeners) {
                listener.process(event);
            }

            long glfwWindow = event.getContext().getGlfwWindow();
            if (event instanceof WindowCloseEvent && LeguiSystem.isInitialized()) {
                Window window = LeguiSystem.getWindow(glfwWindow);
                if (window != null) {
                    for (EventListener<WindowCloseEvent> listener : window.getWindowCloseEventListeners()) {
                        listener.process((WindowCloseEvent) event);
                    }
                }
            }
        }
    }

    /**
     * Used to push event to event processor.
     *
     * @param event event to push to event processor.
     */
    @Override
    public void pushEvent(Event event) {
        if (event.getContext() != null && event.getContext().isDebugEnabled()) {
            LOGGER.debug(event);
        }
        eventQueue.add(event);
    }

    /**
     * Returns true if there are events that should be processed.
     *
     * @return true if there are events that should be processed.
     */
    @Override
    public boolean hasEvents() {
        return !eventQueue.isEmpty();
    }
}
