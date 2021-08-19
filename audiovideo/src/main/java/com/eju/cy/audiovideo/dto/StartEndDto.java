package com.eju.cy.audiovideo.dto;

public class StartEndDto {

    private   int   startIndenx;
    private   int   endIndenx;

    public int getStartIndenx() {
        return startIndenx;
    }

    public void setStartIndenx(int startIndenx) {
        this.startIndenx = startIndenx;
    }

    public int getEndIndenx() {
        return endIndenx;
    }

    public void setEndIndenx(int endIndenx) {
        this.endIndenx = endIndenx;
    }


    @Override
    public String toString() {
        return "StartEndDto{" +
                "startIndenx=" + startIndenx +
                ", endIndenx=" + endIndenx +
                '}';
    }
}
