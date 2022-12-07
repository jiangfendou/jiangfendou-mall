package com.jiangfendou.mall.search.web;

import com.jiangfendou.mall.search.service.MallSearchService;
import com.jiangfendou.mall.search.vo.SearchParamVo;
import com.jiangfendou.mall.search.vo.SearchResult;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping({"/", "/list.html"})
    public String listPage(SearchParamVo searchParamVo, Model model, HttpServletRequest httpServletRequest) {
        String queryString = httpServletRequest.getQueryString();
        searchParamVo.set_queryString(queryString);
        SearchResult result = mallSearchService.search(searchParamVo);
        model.addAttribute("result", result);
        return "list";
    }
}
