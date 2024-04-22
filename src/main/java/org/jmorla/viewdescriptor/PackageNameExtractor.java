package org.jmorla.viewdescriptor;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

public class PackageNameExtractor {
    private final Elements elementUtils;

    public PackageNameExtractor(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    public String getPackageName(Element typeElement) {
        return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
    }
}