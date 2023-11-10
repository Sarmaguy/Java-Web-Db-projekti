package hr.fer.zemris.java.p12.model;

import java.util.List;

public class Poll {
    private long id;
    private String title;
    private String message;
    private List<PollOption> pollOptions;

    public Poll(long id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }

    public Poll(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void setPollOptions (List<PollOption> pollOptions) {
        this.pollOptions = pollOptions;
    }

    public List<PollOption> getPollOptions() {
        return pollOptions;
    }



}
