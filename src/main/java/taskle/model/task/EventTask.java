package taskle.model.task;

import taskle.model.tag.UniqueTagList;

public class EventTask extends Task {

    public EventTask(Name name, UniqueTagList tags) {
        super(name, tags);
    }

    public EventTask(ReadOnlyTask source) {
        super(source);
    }

    public EventTask(ModifiableTask source) {
        super(source);
    }

    @Override
    public String getDetailsString() {
        return null;
    }

}
