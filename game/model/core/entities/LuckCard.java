package model.core.entities;

enum LuckType { LUCKY, MISFORTUNE }

abstract class LuckCard {
    private final LuckType type;
    private final String story;

    public LuckCard(LuckType type, String story) {
        this.type = type;
        this.story = story;
    }

    public LuckType getType() { return type; }
    public String getStory()  { return story; }
    public abstract boolean use(Player player);
    public abstract boolean onDraw(Player player);
}

abstract class AutomaticCard extends LuckCard {
    public AutomaticCard(LuckType type, String story) {
        super(type, story);
    }

    @Override
    public boolean onDraw(Player player) {
        return this.use(player);
    }
}

abstract class ManualCard extends LuckCard {
    public ManualCard(LuckType type, String story) {
        super(type, story);
    }

    @Override
    public boolean onDraw(Player player) {
        return false;
    }
}