package com.tencent.supersonic.chat.api.pojo.response;

import java.util.List;
import lombok.Data;


@Data
public class SearchResp {

    private List<SearchResult> searchResults;

    public SearchResp(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }
}
