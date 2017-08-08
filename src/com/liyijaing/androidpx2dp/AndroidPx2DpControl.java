package com.liyijaing.androidpx2dp;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class AndroidPx2DpControl {

    private Project project;

    public AndroidPx2DpControl(Project project) {
        this.project = project;
    }


    public void changePx2Percent(int screenw, int screenh, boolean percentW) {


        try {
            com.intellij.openapi.editor.Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            Document document = editor.getDocument();


            VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            String filename = file.getName();
            if (!filename.endsWith(".xml")) return;
            System.out.println("file name = " + file.getName());

            org.dom4j.Document documentxml = DocumentHelper.parseText(document.getText());
            Element root = documentxml.getRootElement();
            String rootname = root.getName();
            if (!rootname.endsWith("Layout")) return;


            org.dom4j.Document documentout = DocumentHelper.createDocument();

            String elementName = root.getName();
            String newElementName = "com.zhy.android.percent.support.Percent";
            if (elementName.equals("LinearLayout") || elementName.equals("RelativeLayout") || elementName.equals("FrameLayout")) {
                newElementName = newElementName + elementName;
            } else {
                newElementName = elementName;
            }


            Element rootout = documentout.addElement(newElementName);

            List attributes = root.attributes();

            for (Object object : attributes) {
                Attribute attribute = (Attribute) object;
                addAttr(rootout, attribute);
            }

            rootout.addNamespace("android", "http://schemas.android.com/apk/res/android");
            rootout.addNamespace("tools", "http://schemas.android.com/tools");
            rootout.addNamespace("app", "http://schemas.android.com/apk/res-auto");

            List namespace = root.getNamespacesForURI("http://schemas.android.com/apk/res-auto");
            for (Object object : namespace) {
                Namespace namespacetemp = (Namespace) object;
                rootout.add(namespacetemp);
            }

            System.out.println(root.getNamespacesForURI("http://schemas.android.com/apk/res/android"));
            System.out.println(root.getNamespacesForURI("http://schemas.android.com/apk/res-auto"));
            System.out.println(root.getNamespacesForURI("http://schemas.android.com/tools"));

            percentElement(root, screenw, screenh, rootout, percentW);

            OutputFormat format = new OutputFormat();
            format.setEncoding("UTF-8");
            format.setIndent(true);
            format.setNewlines(true);
            format.setLineSeparator("\t\n");
            format.setNewLineAfterDeclaration(true);
            format.setNewLineAfterNTags(1);
            StringWriter stringWriter = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
            xmlWriter.write(documentout);
            xmlWriter.flush();
            xmlWriter.close();

            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    document.setText(stringWriter.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void cleanPercent() {


        try {
            com.intellij.openapi.editor.Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            Document document = editor.getDocument();
            VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            String filename = file.getName();
            if (!filename.endsWith(".xml")) return;

            org.dom4j.Document documentxml = DocumentHelper.parseText(document.getText());
            Element root = documentxml.getRootElement();
            String rootname = root.getName();
            if (!rootname.endsWith("Layout")) return;
            org.dom4j.Document documentout = DocumentHelper.createDocument();

            String elementName = root.getName();
            String newElementName = "com.zhy.android.percent.support.Percent";
            if (elementName.startsWith(newElementName)) {
                elementName = elementName.replace(newElementName, "");
            } else {

            }


            Element rootout = documentout.addElement(elementName);
            List attributes = root.attributes();

            for (Object object : attributes) {
                Attribute attribute = (Attribute) object;
                String text = attribute.getText();
                if (text.endsWith("%w") || text.endsWith("%h")) {

                } else {
                    addAttr(rootout, attribute);
                }
            }

            rootout.addNamespace("android", "http://schemas.android.com/apk/res/android");
            rootout.addNamespace("tools", "http://schemas.android.com/tools");
            rootout.addNamespace("app", "http://schemas.android.com/apk/res-auto");

            List namespace = root.getNamespacesForURI("http://schemas.android.com/apk/res-auto");
            for (Object object : namespace) {
                Namespace namespacetemp = (Namespace) object;
                rootout.add(namespacetemp);
            }

//            System.out.println(root.getNamespacesForURI("http://schemas.android.com/apk/res/android"));
//            System.out.println(root.getNamespacesForURI("http://schemas.android.com/apk/res-auto"));
//            System.out.println(root.getNamespacesForURI("http://schemas.android.com/tools"));

            dePercentElement(root, rootout);

            OutputFormat format = new OutputFormat();
            format.setEncoding("UTF-8");
            format.setIndent(true);
            format.setNewlines(true);
            format.setLineSeparator("\t\n");
            format.setNewLineAfterDeclaration(true);
            format.setNewLineAfterNTags(1);
            StringWriter stringWriter = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
            xmlWriter.write(documentout);
            xmlWriter.flush();
            xmlWriter.close();

            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    document.setText(stringWriter.toString());
                }
            });

//            document.setText(stringWriter.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void dePercentElement(Element inroot, Element outroot) {
        for (int i = 0, size = inroot.nodeCount(); i < size; i++) {
            Node node = inroot.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;

                String elementName = element.getName();
                String newElementName = "com.zhy.android.percent.support.Percent";
                if (elementName.startsWith(newElementName)) {
                    elementName = elementName.replace(newElementName, "");
                } else {

                }
                Element elementout = outroot.addElement(elementName);
                System.out.println("xxxx:" + elementout.getName());
                List attributes = element.attributes();

                for (Object object : attributes) {
                    Attribute attribute = (Attribute) object;

                    String text = attribute.getText();
                    if (text.endsWith("%w") || text.endsWith("%h")) {

                    } else {
                        addAttr(elementout, attribute);
                    }

                }

                if (element.elements().size() > 0) {
                    dePercentElement(element, elementout);
                }

            }

        }

    }


    public void dimensChange(float swscale, int swbase, int[] configarr, ArrayList<Integer> platforms) {


        try {
            System.out.println("swscale = " + swscale + "swbase= " + swbase);

            //1. 导出 1920或2048的 dp 文件
            com.intellij.openapi.editor.Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            Document document = editor.getDocument();
            VirtualFile testfile = FileDocumentManager.getInstance().getFile(document);
            String filename = testfile.getName();
            if (!filename.endsWith(".xml")) return;

            org.dom4j.Document documentin = DocumentHelper.parseText(document.getText());
            org.dom4j.Document documentout = DocumentHelper.createDocument();

            Element root = documentin.getRootElement();
            String rootname = root.getName();
            if (!rootname.equals("resources")) return;

            Element rootout = documentout.addElement(root.getName());
            for (int i = 0, size = root.nodeCount(); i < size; i++) {
                Node node = root.node(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    Element elementout = rootout.addElement(element.getName());
                    elementout.addAttribute("name", element.attributeValue("name"));

                    String text = element.getText();

                    if (text.contains("px")) {
                        int old_px = Integer.parseInt(text.replace("px", ""));
                        int new_px = (int) Math.floor((float) old_px / swscale);
                        text = new_px + "dp";
                    }
                    elementout.addText(text);

                } else if (node instanceof Comment) {
                    Comment comment = (Comment) node;
                    rootout.addComment(comment.getText());

                }
            }

            //2. 生成 其他 的dp 文件
            VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            VirtualFile resfile = file.getParent().getParent();


            if (resfile.getName().equals("res") && resfile.isDirectory()) {
                System.out.println("delete files");
                for (int i = 0; i < configarr.length; i++) {
                    int platform = configarr[i];
//                    values-sw320dp  dimens.xml
                    String childName = "values-sw" + platform + "dp";

//                    System.out.println("childName" + childName);
                    String path = resfile.getPath();
//                    System.out.println("path" + path);
                    File childFile = new File(path, childName);
                    if (childFile.exists()) {
                        System.out.println("delete:" + childFile.getPath());
                        removeDirectory(childFile);
                    }
                }
                System.out.println("create files");
                for (int i = 0; i < platforms.size(); i++) {
                    int platform = platforms.get(i);
//                    values-sw320dp  dimens.xml
                    String childName = "values-sw" + platform + "dp";

//                    System.out.println("childName" + childName);
                    String path = resfile.getPath();
//                    System.out.println("path" + path);
                    File childFile = new File(path, childName);
                    if (!childFile.exists()) {
                        System.out.println("create:" + childFile.getPath());
                        childFile.mkdir();
                    }

                    float scale = (float) platform / (float) swbase;
                    System.out.println("scale = " + scale);

                    org.dom4j.Document documentoutchild = DocumentHelper.createDocument();
                    Element rootoutchild = documentoutchild.addElement(rootout.getName());

                    for (int j = 0, size = rootout.nodeCount(); j < size; j++) {
                        Node node = rootout.node(j);
                        if (node instanceof Element) {
                            Element element = (Element) node;
                            Element elementout = rootoutchild.addElement(element.getName());
                            elementout.addAttribute("name", element.attributeValue("name"));

                            String text = element.getText();

                            if (text.contains("dp")) {
                                int old_px = Integer.parseInt(text.replace("dp", ""));
                                int new_px = 0;
                                new_px = (int) Math.round(old_px * scale);
                                text = new_px + "dp";

                            }
                            elementout.addText(text);

                        } else if (node instanceof Comment) {
                            Comment comment = (Comment) node;
                            rootoutchild.addComment(comment.getText());

                        }
                    }


                    OutputFormat format = OutputFormat.createPrettyPrint();
                    format.setEncoding("UTF-8");
                    format.setNewlines(true);
                    format.setIndent(true);
                    format.setIndent("    ");

                    File fileout = new File(childFile, "dimens.xml");
                    fileout.createNewFile();
                    XMLWriter out = new XMLWriter(new FileWriter(fileout), format);

                    out.write(documentoutchild);
                    out.flush();
                    out.close();

                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void removeDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    removeDirectory(files[i]);
                }
                files[i].delete();
            }
            directory.delete();
        }
    }


    private void addAttr(Element elementout, Attribute attribute) {
        if (!attribute.getNamespacePrefix().equals("")) {
            elementout.addAttribute(attribute.getNamespacePrefix() + ":" + attribute.getName(), attribute.getText());
        } else {
            elementout.addAttribute(attribute.getName(), attribute.getText());
        }
    }

    private void percentElement(Element inroot, int parent_w, int parent_h, Element outroot, boolean percent_w) {
        for (int i = 0, size = inroot.nodeCount(); i < size; i++) {
            Node node = inroot.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;

                String elementName = element.getName();
                String newElementName = "com.zhy.android.percent.support.Percent";
                if (elementName.equals("LinearLayout") || elementName.equals("RelativeLayout") || elementName.equals("FrameLayout")) {
                    newElementName = newElementName + elementName;
                } else {
                    newElementName = elementName;
                }
                Element elementout = outroot.addElement(newElementName);
                List attributes = element.attributes();
                int in_element_w = parent_w;
                int in_element_h = parent_h;

                for (Object object : attributes) {
                    Attribute attribute = (Attribute) object;

                    String text = attribute.getText();
                    if (text.endsWith("px")) {
//                        log.append(elementout.getName() + "---" + element.attributeValue("id") + "---" + attribute.getName() + "\n");
//                        log.selectAll();
//                        log.setCaretPosition(log.getSelectedText().length());
//                        log.requestFocus();

                        int old_px = Integer.parseInt(text.replace("px", ""));
                        if (old_px < 0) {
                            addAttr(elementout, attribute);
                        } else {

                            float percent;
                            if (percent_w) {
                                percent = (float) old_px / (float) parent_w;
                                text = String.format("%.2f", percent * 100) + "%w";

                            } else {
                                percent = (float) old_px / (float) parent_h;
                                text = String.format("%.2f", percent * 100) + "%h";
                            }

                            System.out.println("percent:" + percent + "---old_px:" + old_px + "---parent_w:" + parent_w);

                            if (attribute.getName().equals("layout_width")) {
                                in_element_w = old_px;
                                elementout.addAttribute("app:layout_widthPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().equals("layout_height")) {
                                in_element_h = old_px;
                                elementout.addAttribute("app:layout_heightPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().equals("layout_margin")) {
                                elementout.addAttribute("app:layout_marginPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().equals("layout_marginTop")) {
                                elementout.addAttribute("app:layout_marginTopPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().equals("layout_marginLeft")) {
                                elementout.addAttribute("app:layout_marginLeftPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().equals("layout_marginBottom")) {
                                elementout.addAttribute("app:layout_marginBottomPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().equals("layout_marginRight")) {
                                elementout.addAttribute("app:layout_marginRightPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().equals("layout_marginEnd")) {
                                elementout.addAttribute("app:layout_marginEndPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().equals("layout_marginStart")) {
                                elementout.addAttribute("layout_marginStartPercent", text);
                                addAttr(elementout, attribute);
                            } else if (attribute.getName().contains("padding")) {
                                addAttr(elementout, attribute);
                            }
                        }

                    } else {
                        addAttr(elementout, attribute);

                    }

                }

                if (element.elements().size() > 0) {
                    percentElement(element, in_element_w, in_element_h, elementout, percent_w);
                }

            }

        }
    }


}
