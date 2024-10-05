package com.vpolosov.trainee.mergexml.service;

import com.vpolosov.trainee.mergexml.aspect.Loggable;
import com.vpolosov.trainee.mergexml.config.ConfigProperties;
import com.vpolosov.trainee.mergexml.dtos.ValidatedDocumentsDto;
import com.vpolosov.trainee.mergexml.handler.exception.MoreFiveHundredKbException;
import com.vpolosov.trainee.mergexml.utils.DocumentUtil;
import com.vpolosov.trainee.mergexml.utils.FileUtil;
import com.vpolosov.trainee.mergexml.utils.TransformerUtil;
import com.vpolosov.trainee.mergexml.validators.CheckFileSize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.vpolosov.trainee.mergexml.utils.Constant.EMPTY_SIZE;
import static com.vpolosov.trainee.mergexml.utils.Constant.FIRST_ELEMENT;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.BS_HEAD;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.BS_MESSAGE;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.DATE_TIME;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.DOCUMENTS;
import static com.vpolosov.trainee.mergexml.utils.XmlTags.ID;

/**
 * Сервис объединения платёжных документов.
 *
 * @author Ali Takushinov
 * @author Maksim Litvinenko
 */
@Service
@RequiredArgsConstructor
public class MergeService {

    /**
     * Вспомогательный класс для работы с файлами.
     */
    private final FileUtil fileUtil;

    /**
     * Вспомогательный класс для работы с {@link Document}.
     */
    private final DocumentUtil documentUtil;

    /**
     * Вспомогательный класс для работы с {@link Transformer}.
     */
    private final TransformerUtil transformerUtil;

    /**
     * Проверка файла на размер.
     */
    private final CheckFileSize checkFileSize;

    /**
     * Свойства приложения.
     */
    private final ConfigProperties configProperties;

    /**
     * Часы для корректировки времени.
     */
    private final Clock clock;

    /**
     * Парсер для результирующего файла.
     */
    private final DateTimeFormatter totalTimeFormat;

    /**
     * Объединяет XML файлы в каталоге для создания платёжного документа.
     *
     * @param validatedDocumentsDto
     * @return объединённый документ платёжных операций.
     * @throws MoreFiveHundredKbException если размер объединённого файла больше 500 кб.
     */
    @Loggable
    public Document merge(ValidatedDocumentsDto validatedDocumentsDto) {
        Document targetDocument = documentUtil.create();

        for (var document : validatedDocumentsDto.documents()) {
            aggregateTotal(document, targetDocument);
        }

        targetDocument.normalizeDocument();
        targetDocument.getElementsByTagName(BS_MESSAGE)
            .item(FIRST_ELEMENT)
            .getAttributes()
            .getNamedItem(ID)
            .setNodeValue(UUID.randomUUID().toString());
        targetDocument.getElementsByTagName(BS_MESSAGE)
            .item(FIRST_ELEMENT)
            .getAttributes()
            .getNamedItem(DATE_TIME)
            .setNodeValue(LocalDateTime.now().toString());


        targetDocument.normalize();
        DOMSource dom = new DOMSource(targetDocument);

        var fileName = fileUtil.fileNameWithTime(configProperties.getFileName(), clock, totalTimeFormat);
        var total = new File(validatedDocumentsDto.path(), fileName);
        transformerUtil.transform(dom, new StreamResult(total));

        if (checkFileSize.isMoreThanFiveKb(total)) {
            fileUtil.delete(total);
            throw new MoreFiveHundredKbException("There are more than 500 kb files");
        }
        return targetDocument;
    }

    /**
     * Формирование общего документа с информацией о платёжных операциях.
     *
     * @param document       содержит информацию о платёжной операции.
     * @param targetDocument конечный документ, в который объединяется информация о платёжных операциях.
     */
    @Loggable
    private void aggregateTotal(Document document, Document targetDocument) {
        document.getDocumentElement().normalize();
        if (targetDocument.getElementsByTagName(DOCUMENTS).getLength() == EMPTY_SIZE) {
            NodeList documentNodeList = document.getChildNodes();
            Node targetNode = targetDocument.importNode(documentNodeList.item(FIRST_ELEMENT), true);
            targetDocument.appendChild(targetNode);
        } else {
            NodeList headerNodeList = document.getElementsByTagName(BS_HEAD)
                .item(FIRST_ELEMENT)
                .getChildNodes();
            for (int i = FIRST_ELEMENT; i < headerNodeList.getLength(); i++) {
                Node headerNode = headerNodeList.item(i);
                Node targetHeaderNode = targetDocument.importNode(headerNode, true);

                Element targetHeaderElement = (Element) targetDocument
                    .getElementsByTagName(BS_HEAD)
                    .item(FIRST_ELEMENT);
                targetHeaderElement.appendChild(targetHeaderNode);
            }

            NodeList documentNodeList = document.getElementsByTagName(DOCUMENTS)
                .item(FIRST_ELEMENT)
                .getChildNodes();

            for (int i = FIRST_ELEMENT; i < documentNodeList.getLength(); i++) {
                Node documentNode = documentNodeList.item(i);
                Node targetDocumentNode = targetDocument.importNode(documentNode, true);
                targetDocument.normalize();
                Element targetDocumentElement = (Element) targetDocument
                    .getElementsByTagName(DOCUMENTS)
                    .item(FIRST_ELEMENT);
                targetDocumentElement.appendChild(targetDocumentNode);
            }
        }
    }
}
