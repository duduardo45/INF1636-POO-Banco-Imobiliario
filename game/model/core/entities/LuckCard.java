package model.core.entities;

enum LuckType { LUCKY, MISFORTUNE }

abstract class LuckCard {
    private final String imageId;
    private final LuckType type;
    private final String story;

    public LuckCard(String imageId, LuckType type, String story) {
        this.imageId = imageId;
        this.type = type;
        this.story = story;
    }

    public LuckType getType() { return type; }
    public String getStory()  { return story; }
    public abstract boolean use(Player player);
    public abstract boolean onDraw(Player player);
}

abstract class AutomaticCard extends LuckCard {
    public AutomaticCard(String imageId, LuckType type, String story) {
        super(imageId, type, story);
    }

    @Override
    public boolean onDraw(Player player) {
        return this.use(player);
    }
}

abstract class ManualCard extends LuckCard {
    public ManualCard(String imageId, LuckType type, String story) {
        super(imageId, type, story);
    }

    @Override
    public boolean onDraw(Player player) {
        return false;
    }
}