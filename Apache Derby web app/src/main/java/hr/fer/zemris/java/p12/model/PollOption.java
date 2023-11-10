package hr.fer.zemris.java.p12.model;

public class PollOption {
    private long id;
    private String optionTitle;
    private String optionLink;
    private long pollId;
    private long votesCount;

    public PollOption(long id, String optionTitle, String optionLink, long pollId, long votesCount) {
        this.id = id;
        this.optionTitle = optionTitle;
        this.optionLink = optionLink;
        this.pollId = pollId;
        this.votesCount = votesCount;
    }

    public PollOption(String optionTitle, String optionLink, long pollId, long votesCount) {
        this.optionTitle = optionTitle;
        this.optionLink = optionLink;
        this.pollId = pollId;
        this.votesCount = votesCount;
    }

    public long getId() {
        return id;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public String getOptionLink() {
        return optionLink;
    }

    public long getPollId() {
        return pollId;
    }

    public long getVotesCount() {
        return votesCount;
    }

}
