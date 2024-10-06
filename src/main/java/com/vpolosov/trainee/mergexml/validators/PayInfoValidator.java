package com.vpolosov.trainee.mergexml.validators;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.config.GraphConfig;
import com.vpolosov.trainee.mergexml.dtos.ValidateDocumentDto;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.utils.Vertex;
import com.vpolosov.trainee.mergexml.validators.api.Validation;
import com.vpolosov.trainee.mergexml.validators.api.ValidationContext;
import lombok.RequiredArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import static com.vpolosov.trainee.mergexml.utils.XmlTags.CODEREV;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.PAYGRNDPARAM;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.PAYTYPEPARAM;

/**
 * Проверка на соответствие кода программы доходов бюджетов, типа платежа и
 * основания платежа на связь, которая определена в {@link GraphConfig#graph}.
 *
 * @author Maksim Litvinenko
 */
@Component
@RequiredArgsConstructor
public class PayInfoValidator implements Validation<ValidateDocumentDto> {

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    /**
     * Граф зависимостей.
     */
    private final Graph<Vertex, DefaultEdge> graph;

    @Loggable
    @Override
    public boolean validate(ValidateDocumentDto validateDocumentDto, ValidationContext context) {
        var coderev = documentUtil.getValueByTagName(validateDocumentDto.document(), CODEREV);
        var payTypeParam = documentUtil.getValueByTagName(validateDocumentDto.document(), PAYTYPEPARAM);
        var payGrndParam = documentUtil.getValueByTagName(validateDocumentDto.document(), PAYGRNDPARAM);
        var vCodeRev = new Vertex(coderev, CODEREV);
        var vPayTypeParam = new Vertex(payTypeParam, PAYTYPEPARAM);
        var vPayGrndParam = new Vertex(payGrndParam, PAYGRNDPARAM);
        if (!graph.containsVertex(vCodeRev)) {
            context.addMessage("Код программы доходов бюджетов не найден в графе зависимостей", CODEREV);
            return false;
        }
        if (!graph.containsVertex(vPayTypeParam)) {
            context.addMessage("Тип платежа не найден в графе зависимостей", PAYTYPEPARAM);
            return false;
        }
        if (!graph.containsVertex(vPayGrndParam)) {
            context.addMessage("Основание платежа не найдено в графе зависимостей", PAYGRNDPARAM);
            return false;
        }
        if (!(graph.containsEdge(vCodeRev, vPayTypeParam) && graph.containsEdge(vPayTypeParam, vPayGrndParam))) {
            context.addMessage("Код программы доходов бюджетов, тип платежа и основание платежа не имеют единой связи", CODEREV, PAYTYPEPARAM, PAYGRNDPARAM);
            return false;
        }
        return true;
    }
}
