package com.example.kunworld.api;

import java.util.List;

public class QuoteResponse {
    private int version;
    private List<String> quotes;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<String> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<String> quotes) {
        this.quotes = quotes;
    }
}
