package com.vpolosov.trainee.mergexml.service;

import java.io.File;
import java.util.List;

/**
 * Класс для хранения информации о файлах XML и XSD.
 * Содержит список XML файлов и один XSD файл для валидации.
 *
 * @author Artyom Bogaichuk
 *
 * @param xmlFiles Список XML файлов.
 * @param xsdFile  XSD файл для валидации XML файлов.
 */
public record FilesInfo(List<File> xmlFiles, File xsdFile) {
}
