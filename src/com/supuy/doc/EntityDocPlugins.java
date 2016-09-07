package com.supuy.doc;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.GroupedElementsRenderer;

/**
 * PACKAGE_NAME
 * Created by bill on 2016/5/12.
 */
public class EntityDocPlugins extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Application application = ApplicationManager.getApplication();

        SPSComponent component = application.getComponent(SPSComponent.class);

        component.generateGSMethod(component.getPsiMethodFromContext(e));

    }

}
