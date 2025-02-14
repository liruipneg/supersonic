package com.tencent.supersonic.headless.server.rest;

import com.tencent.supersonic.auth.api.authentication.pojo.User;
import com.tencent.supersonic.auth.api.authentication.utils.UserHolder;
import com.tencent.supersonic.headless.api.pojo.request.ViewReq;
import com.tencent.supersonic.headless.api.pojo.response.ViewResp;
import com.tencent.supersonic.headless.server.pojo.MetaFilter;
import com.tencent.supersonic.headless.server.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/semantic/view")
public class ViewController {

    @Autowired
    private ViewService viewService;

    @PostMapping
    public ViewResp save(@RequestBody ViewReq viewReq,
                     HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return viewService.save(viewReq, user);
    }

    @PutMapping
    public ViewResp update(@RequestBody ViewReq viewReq,
                           HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        return viewService.update(viewReq, user);
    }

    @GetMapping("/{id}")
    public ViewResp getView(@PathVariable("id") Long id) {
        return viewService.getView(id);
    }

    @GetMapping("/getViewList")
    public List<ViewResp> getViewList(@RequestParam("domainId") Long domainId) {
        MetaFilter metaFilter = new MetaFilter();
        metaFilter.setDomainId(domainId);
        return viewService.getViewList(metaFilter);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable("id") Long id,
                       HttpServletRequest request, HttpServletResponse response) {
        User user = UserHolder.findUser(request, response);
        viewService.delete(id, user);
        return true;
    }

}
