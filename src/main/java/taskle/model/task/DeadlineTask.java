package taskle.model.task;

import taskle.model.tag.UniqueTagList;

public class DeadlineTask extends Task {

    public DeadlineTask(Name name, UniqueTagList tags) {
        super(name, tags);
    }

    public DeadlineTask(ReadOnlyTask source) {
        super(source);
    }

    public DeadlineTask(ModifiableTask source) {
        super(source);
    }

    @Override
    public String getDetailsString() {
        // TODO Auto-generated method stub
        return null;
    }

}
