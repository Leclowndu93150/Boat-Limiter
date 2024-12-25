package com.leclowndu93150.boat_limiter;

public interface BoatJumpAccessor {
    float getJumpPower();
    void setJumping(boolean jumping);
    boolean isJumping();
    void setJumpPower(float power);
    int getJumpRechargeTicks();
    void setJumpRechargeTicks(int ticks);
}