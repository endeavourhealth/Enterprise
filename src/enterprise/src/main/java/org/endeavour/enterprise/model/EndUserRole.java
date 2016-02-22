package org.endeavour.enterprise.model;

public enum EndUserRole
{
    USER(1),
    ADMIN(2);
    //SUPER(4); //2016-02-22 DL - removed, since super user is now a property of the EndUser as a whole

    private int value;

    EndUserRole(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public boolean isGreaterThanOrEqualTo(EndUserRole endUserRole)
    {
        return (value >= endUserRole.getValue());
    }
/*
    EndUserRole getValue(int value) {
        for(EndUserRole e: EndUserRole.values()) {
            if(e.value == value) {
                return e;
            }
        }
        return null;// not found
    }*/
}
