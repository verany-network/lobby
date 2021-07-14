package space.mcplay.lobby.reward.collection;

import java.util.concurrent.TimeUnit;

public enum CreditReward {

  HOURLY("hourlyReward", 5, 11, TimeUnit.HOURS.toMillis(1)),
  DAILY("dailyReward", 30, 15, TimeUnit.DAYS.toMillis(1));

  private final String profileValue;
  private final int defaultReward, slot;
  private final long coolDown;

  CreditReward(String profileValue, int defaultReward, int slot, long coolDown) {
    this.profileValue = profileValue;
    this.defaultReward = defaultReward;
    this.slot = slot;
    this.coolDown = coolDown;
  }

  public String getProfileValue() {
    return this.profileValue;
  }

  public int getDefaultReward() {
    return this.defaultReward;
  }

  public int getSlot() {
    return this.slot;
  }

  public long getCoolDown() {
    return this.coolDown;
  }
}
