package org.endeavour.enterprise.model;

public enum Role
{
    USER(1),
    ADMIN(2),
    SUPER(4);

    private int value;

    Role(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
