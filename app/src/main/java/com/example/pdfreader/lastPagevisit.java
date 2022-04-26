package com.example.pdfreader;

public class lastPagevisit {

    private int lastPage;

    public synchronized void setlastPage(int lastPage){
        this.lastPage = lastPage;
    }

    public int getLastPage() {
        return lastPage;
    }
}
