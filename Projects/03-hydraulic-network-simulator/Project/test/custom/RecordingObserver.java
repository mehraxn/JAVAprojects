package custom;

import java.util.HashMap;
import java.util.Map;

import hydraulic.SimulationObserver;

final class RecordingObserver implements SimulationObserver {
    static final class Event {
        final double input;
        final double[] outputs;

        Event(double input, double[] outputs) {
            this.input = input;
            this.outputs = outputs.clone();
        }
    }

    final Map<String, Event> statuses = new HashMap<>();
    final Map<String, Event> errors = new HashMap<>();

    @Override
    public void notify(Level level, String type, String name, double inFlow, double... flows) {
        Event event = new Event(inFlow, flows);
        if (level == Level.ERROR) {
            errors.put(name, event);
        } else {
            statuses.put(name, event);
        }
    }
}
