package com.tencent.supersonic.headless.api.pojo;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewModelConfig {

    private Long id;

    private boolean includesAll;

    private List<Long> metrics = Lists.newArrayList();

    private List<Long> dimensions = Lists.newArrayList();

    public ViewModelConfig(Long id, boolean includesAll) {
        this.id = id;
        this.includesAll = includesAll;
    }
}
