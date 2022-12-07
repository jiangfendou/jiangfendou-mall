package com.jiangfendou.mall.search.service;

import com.jiangfendou.mall.search.vo.SearchParamVo;
import com.jiangfendou.mall.search.vo.SearchResult;

public interface MallSearchService {

    /**
     * search product list
     * @param   searchParamVo searchParamVo
     * @return  SearchResult SearchResult
     * */
    SearchResult search(SearchParamVo searchParamVo);
}
