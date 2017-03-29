package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON object used to manipulate folders, such as creating, moving and renaming
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonTermlexSearchResult {

    private List<JsonTermlexSearchResultCategory> categories = new ArrayList<>();
    private List<JsonCode> results = new ArrayList<>();
    private Integer searchTime = null;
    private Boolean showingSuggestions = null;
    private Integer totalHits = null;

    public JsonTermlexSearchResult() {
    }

    /**
     * gets/sets
     */

    public List<JsonTermlexSearchResultCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<JsonTermlexSearchResultCategory> categories) {
        this.categories = categories;
    }

    public List<JsonCode> getResults() {
        return results;
    }

    public void setResults(List<JsonCode> results) {
        this.results = results;
    }

    public Integer getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Integer searchTime) {
        this.searchTime = searchTime;
    }

    public Boolean getShowingSuggestions() {
        return showingSuggestions;
    }

    public void setShowingSuggestions(Boolean showingSuggestions) {
        this.showingSuggestions = showingSuggestions;
    }

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }


}
