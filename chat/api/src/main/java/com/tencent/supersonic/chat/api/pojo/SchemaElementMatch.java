package com.tencent.supersonic.chat.api.pojo;

import com.tencent.supersonic.headless.api.pojo.SchemaElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaElementMatch {

    SchemaElement element;
    double similarity;
    String detectWord;
    String word;
    Long frequency;
    boolean isInherited;

}
