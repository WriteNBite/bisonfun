package com.bisonfun.utilities;

import java.util.List;

public class Pagination<T> {
    private int currentPage;
    private int count;
    private List<T> documents;
    private int lastPage;

    public Pagination(int currentPage, int count, List<T> documents, int lastPage){
        this.currentPage = currentPage;
        this.count = count;
        this.documents = documents;
        this.lastPage = lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getDocuments() {
        return documents;
    }

    public void setDocuments(List<T> documents) {
        this.documents = documents;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

}
