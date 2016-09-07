package com.supuy.doc;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.CollectionListModel;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PACKAGE_NAME
 * Created by bill on 2016/5/12.
 */
public class SPSComponent implements ApplicationComponent {
    public SPSComponent() {
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @NotNull
    public String getComponentName() {
        return "com.supuy.doc.SPSComponent";
    }

    /**
     * 启动写线程
     *
     * @param psiMethod
     */
    public void generateGSMethod(final PsiClass psiMethod) {
        new WriteCommandAction.Simple(psiMethod.getProject(), psiMethod.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                createGetSet(psiMethod);
            }
        }.execute();
    }

    public void createGetSet(PsiClass psiClass) throws Exception {
        List<PsiField> fields = new CollectionListModel<PsiField>(psiClass.getFields()).getItems();
        if (fields == null) {
            return;
        }
        List<PsiMethod> list = new CollectionListModel<PsiMethod>(psiClass.getMethods()).getItems();
        Set<String> methodSet = new HashSet<String>();
        for (PsiMethod m : list) {
            methodSet.add(m.getName());
        }
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

        for (PsiField field : fields) {
            if (field.getModifierList().hasModifierProperty(PsiModifier.FINAL)) {
                continue;
            }
            String methodText = buildGet(field);
            PsiMethod toMethod = elementFactory.createMethodFromText(methodText, psiClass);
            if (methodSet.contains(toMethod.getName())) {
                continue;
            }
            psiClass.add(toMethod);

            methodText = buildSet(field);
            elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
            toMethod = elementFactory.createMethodFromText(methodText, psiClass);
            if (methodSet.contains(toMethod)) {
                continue;
            }
            psiClass.add(toMethod);
        }
    }

    private String buildGet(PsiField field) throws Exception{
        StringBuilder sb = new StringBuilder();
        
        if (field.getDocComment() != null) {
            String [] text = field.getDocComment().getText().split("[\r\n]");
            sb.append(text[0]).append("\n");
            sb.append(text[1].substring(0, 7));
            sb.append("获取");
            sb.append(text[1].substring(7)).append("\n");
            sb.append("* @return ");
            sb.append(field.getName()).append(" ");
            sb.append(text[1].substring(7)).append("\n");
            sb.append(text[2]);
        }
        sb.append("public ");
        //判断字段是否是static
        if (field.getModifierList().hasModifierProperty(PsiModifier.STATIC)) {
            sb.append("static ");
        }
        sb.append(field.getType().getPresentableText() + " ");
        if (field.getType().getPresentableText().equals("boolean")) {
            sb.append("is");
        } else {
            sb.append("get");
        }
        sb.append(getFirstUpperCase(field.getName()));
        sb.append("(){\n");
        sb.append(" return this." + field.getName() + ";}\n");

        return sb.toString();
    }

    private String buildSet(PsiField field) {
        StringBuilder sb = new StringBuilder();
        if (field.getDocComment() != null) {
            String [] text = field.getDocComment().getText().split("[\r\n]");
            sb.append(text[0]).append("\n");
            sb.append(text[1].substring(0, 7));
            sb.append("设置");
            sb.append(text[1].substring(7)).append("\n");
            sb.append("* @param ");
            sb.append(field.getName()).append(" ");
            sb.append(text[1].substring(7)).append("\n");
            sb.append(text[2]);
        }
        sb.append("public ");
        //判断字段是否是static
        if (field.getModifierList().hasModifierProperty(PsiModifier.STATIC)) {
            sb.append("static ");
        }
        sb.append("void ");
        sb.append("set" + getFirstUpperCase(field.getName()));
        sb.append("(" + field.getType().getPresentableText() + " " + field.getName() + "){\n");
        sb.append("this." + field.getName() + " = " + field.getName() + ";");
        sb.append("}");
        return sb.toString();
    }

    private String getFirstUpperCase(String oldStr) {
        return oldStr.substring(0, 1).toUpperCase() + oldStr.substring(1);
    }

    public PsiClass getPsiMethodFromContext(AnActionEvent e) {
        PsiElement elementAt = getPsiElement(e);
        if (elementAt == null) {
            return null;

        }
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

    private PsiElement getPsiElement(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }
        //用来获取当前光标处的PsiElement
        int offset = editor.getCaretModel().getOffset();
        return psiFile.findElementAt(offset);
    }
}
