package com.steffbeard.totalwar.core;

import org.bukkit.block.BlockFace;

public class Portcullis {
    public Portcullis(String worldName, int x, int z, int y, int width, int height, BlockFace direction, int type, byte data) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        this.width = width;
        this.height = height;
        this.y = y;
        this.direction = direction;
        this.type = type;
        this.data = data;
    }

    public String getWorldName() {
        return worldName;
    }

    public BlockFace getDirection() {
        return direction;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public byte getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Portcullis other = (Portcullis) obj;
        if ((this.worldName == null) ? (other.worldName != null) : !this.worldName.equals(other.worldName)) {
            return false;
        }
        if (this.x != other.x) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (this.direction != other.direction) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.worldName != null ? this.worldName.hashCode() : 0);
        hash = 29 * hash + this.x;
        hash = 29 * hash + this.z;
        hash = 29 * hash + this.y;
        hash = 29 * hash + this.width;
        hash = 29 * hash + this.height;
        hash = 29 * hash + (this.direction != null ? this.direction.hashCode() : 0);
        return hash;
    }

    private final String worldName;
    private final int x, z, width, height, type;
    private int y;
    private final BlockFace direction;
    private final byte data;
}