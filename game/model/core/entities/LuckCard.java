package model.core.entities;

enum LuckType { LUCKY, MISFORTUNE }

class LuckCard {
    private final LuckType type;
    private final String story;

    public LuckCard(LuckType type, String story) {
        this.type = type;
        this.story = story;
    }

    public LuckType getType() { return type; }
    public String getStory()  { return story; }
}
