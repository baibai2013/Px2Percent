package com.liyijaing.androidpx2dp;

import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

public class AndroidPx2DpControl {

    private Project project;

    public AndroidPx2DpControl(Project project) {
        this.project = project;
    }


    //------------------------------------------   write to Dimens ------------------------------------------------------//
    private String donutmentFilename;
    Map<String, String> dimensMap = new HashMap<String, String>();
    boolean isHadDimens = false;

    public void writeDimens(String platform) {

        try {

            isHadDimens  = false;

            com.intellij.openapi.editor.Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            Document document = editor.getDocument();

            VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            String filename = file.getName();
            donutmentFilename = filename.substring(0, filename.indexOf("."));

            if (!filename.endsWith(".xml")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid XML files", NotificationType.INFORMATION));

                return;
            }
            System.out.println("donutmentFilename = " + file.getName() + "-- platform:" + platform);

            org.dom4j.Document documentxml = DocumentHelper.parseText(document.getText());


            Element root = documentxml.getRootElement();
            String rootname = root.getName();
            if (!rootname.endsWith("Layout")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid XML files", NotificationType.INFORMATION));
                return;
            }

            //检查是否有dimens 一般从新生产 dimens
            checkHasDimens(root);
            if(isHadDimens){
                System.out.println("isHadDimens------>>>>>>>>");
                cleanDimens(platform);
            }


            org.dom4j.Document documentout = DocumentHelper.createDocument();
            String elementName = root.getName();


            Element rootout = documentout.addElement(elementName);

            rootout.addNamespace("android", "http://schemas.android.com/apk/res/android");
//            rootout.addNamespace("tools", "http://schemas.android.com/tools");
//            rootout.addNamespace("app", "http://schemas.android.com/apk/res-auto");

            List namespace = root.getNamespacesForURI("http://schemas.android.com/apk/res-auto");
            for (Object object : namespace) {
                Namespace namespacetemp = (Namespace) object;
                rootout.add(namespacetemp);
            }

            List attributes = root.attributes();
            for (Object object : attributes) {
                Attribute attribute = (Attribute) object;
                String text = attribute.getText();

                if (text.endsWith("px") || text.endsWith("dp")) {
                    String dimensName = getDimensName(rootout, attribute, "0");
                    String newText = "@dimen/" + dimensName;
                    dimensMap.put(dimensName, text);
                    addAttrByText(rootout, attribute, newText);

                } else {

                    addAttr(rootout, attribute);
                }
            }

            doWriteDimensElement(null, root, rootout);


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


            VirtualFile resfile = file.getParent().getParent();
            String path = resfile.getPath() + "/" + platform + "/dimens.xml";
            writeResultToDimens(donutmentFilename, path);
            Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Write dimens complete", NotificationType.INFORMATION));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cleanDimens(String platform) {

        try {
            com.intellij.openapi.editor.Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            Document document = editor.getDocument();

            VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            String filename = file.getName();
            donutmentFilename = filename.substring(0, filename.indexOf("."));


            if (!filename.endsWith(".xml")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid XML files", NotificationType.INFORMATION));

                return;
            }
            System.out.println("donutmentFilename = " + file.getName() + "-- platform:" + platform);

            org.dom4j.Document documentxml = DocumentHelper.parseText(document.getText());
            Element root = documentxml.getRootElement();
            String rootname = root.getName();
            if (!rootname.endsWith("Layout")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid XML files", NotificationType.INFORMATION));
                return;
            }


            //1.获得dimens map
            VirtualFile resfile = file.getParent().getParent();
            String path = resfile.getPath() + "/" + platform + "/dimens.xml";
            getDimensMap(documentxml, donutmentFilename, path);

            org.dom4j.Document documentout = DocumentHelper.createDocument();
            String elementName = root.getName();


            Element rootout = documentout.addElement(elementName);

            rootout.addNamespace("android", "http://schemas.android.com/apk/res/android");
//            rootout.addNamespace("tools", "http://schemas.android.com/tools");
//            rootout.addNamespace("app", "http://schemas.android.com/apk/res-auto");

            List namespace = root.getNamespacesForURI("http://schemas.android.com/apk/res-auto");
            for (Object object : namespace) {
                Namespace namespacetemp = (Namespace) object;
                rootout.add(namespacetemp);
            }

            List attributes = root.attributes();
            for (Object object : attributes) {
                Attribute attribute = (Attribute) object;
                String text = attribute.getText();
//                System.out.println("root clean text old "+text);
                if (text.startsWith("@dimen/")) {
                    Object[] keyset = dimensMap.keySet().toArray();
                    for (int j = 0; j < keyset.length; j++) {
                        String key = (String) keyset[j];
                        String value = dimensMap.get(key);
                        if (text.contains(key)) {
                            text = value;
                        }
                    }
                    addAttrByText(rootout, attribute, text);
//                    System.out.println("root clean "+text);
                } else {
                    addAttr(rootout, attribute);
                }
            }

            doCleanDimens(root, rootout);


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
                    Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Clean dimens complete", NotificationType.INFORMATION));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void checkHasDimens(Element inroot){

        for (int i = 0, size = inroot.nodeCount(); i < size; i++) {
            Node node = inroot.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;

                String elementName = element.getName();
                List<Attribute> attributes = element.attributes();
                for (Attribute attribute : attributes) {
                    String text = attribute.getText();

                    if (text.startsWith("@dimen/")) {
                       isHadDimens = true;
                    }

                }
                if (element.elements().size() > 0) {
                    checkHasDimens(element);
                }

            }

        }
    }

    private void doCleanDimens(Element inroot, Element outroot) {
        for (int i = 0, size = inroot.nodeCount(); i < size; i++) {
            Node node = inroot.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;

                String elementName = element.getName();
                Element elementout = outroot.addElement(elementName);
                List<Attribute> attributes = element.attributes();
                for (Attribute attribute : attributes) {
                    String text = attribute.getText();

                    if (text.startsWith("@dimen/")) {
                        Object[] keyset = dimensMap.keySet().toArray();
                        for (int j = 0; j < keyset.length; j++) {
                            String key = (String) keyset[j];
                            String value = dimensMap.get(key);


//                            System.out.println("text:"+text+"--key:"+key+"--value:"+value);
                            if (text.contains(key)) {
                                text = value;
                            }
                        }
                        addAttrByText(elementout, attribute, text);
                    } else {
                        addAttr(elementout, attribute);
                    }

                }
                if (element.elements().size() > 0) {
                    doCleanDimens(element, elementout);
                }

            }

        }
    }


    private void doGetkeyList(ArrayList<String> keylist, Element inroot) {

        for (int i = 0, size = inroot.nodeCount(); i < size; i++) {
            Node node = inroot.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;

                List<Attribute> attributes = element.attributes();
                for (Attribute attribute : attributes) {
                    String text = attribute.getText();

                    if (text.startsWith("@dimen/")) {
                        String[] idstringarrr = text.split("/");
                        String key = idstringarrr[1];
                        keylist.add(key);
//                        System.out.println("add key:"+key);
                    }

                }
                if (element.elements().size() > 0) {
                    doGetkeyList(keylist, element);
                }

            }

        }


    }

    private void getDimensMap(org.dom4j.Document currentDocumentxml, String filename, String fileDir) {
        String intXML = "";
        try {
            dimensMap = new HashMap<String, String>();
            BufferedReader br = new BufferedReader(new FileReader(fileDir));
            String line = null;
            while ((line = br.readLine()) != null) {
                intXML += line;
            }
            br.close();

            org.dom4j.Document document = DocumentHelper.parseText(intXML);
            Element root = document.getRootElement();

            org.dom4j.Document documentout = DocumentHelper.createDocument();
            Element rootout = documentout.addElement(root.getName());

            Element rootlayout = currentDocumentxml.getRootElement();

            //1.查询本layout的dimens 列表
            ArrayList<String> keylist = new ArrayList<>();
            List<Attribute> attributes = rootlayout.attributes();
            for (Attribute attribute : attributes) {
                String text = attribute.getText();

                if (text.startsWith("@dimen/")) {
                    String[] idstringarrr = text.split("/");
                    String key = idstringarrr[1];
                    keylist.add(key);
                    System.out.println("add key:"+key);
                }

            }

            doGetkeyList(keylist, rootlayout);

            //2.set map
            for (int i = 0, size = root.nodeCount(); i < size; i++) {
                Node node = root.node(i);
                if (node instanceof Element) {
                    Element element = (Element) node;

                    String sttributeName = element.attributeValue("name");
//                    System.out.println("getDimensMap sttributeName:" + sttributeName + "--keysize:" + keylist.size());
                    boolean iscontens = false;
                    for (String keytemp : keylist) {
                        if (keytemp.equals(sttributeName)) {
                            iscontens = true;
                            break;
                        }
                    }
                    if (iscontens) {
                        String value = element.getText();
                        dimensMap.put(sttributeName, value);
//                        System.out.println("getDimensMap add key:" + sttributeName + "--value:" + value);
                    }
                } else if (node instanceof Comment) {
                    Comment comment = (Comment) node;

                }
            }


            //3.删除 之前生成的dimens
            String startNodeName = filename + "_begin";
            String endNodeName = filename + "_end";

            boolean catched = false;
            for (int i = 0, size = root.nodeCount(); i < size; i++) {
                Node node = root.node(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if (catched) {
                        String key = element.attributeValue("name");
                        String value = element.getText();

//                        System.out.println("getDimensMap key:"+key+"--value:"+value);
//                        dimensMap.put(key, value);
                        continue;
                    }

                    Element elementout = rootout.addElement(element.getName());
                    elementout.addAttribute("name", element.attributeValue("name"));
                    elementout.addText(element.getText());
                } else if (node instanceof Comment) {
                    Comment comment = (Comment) node;

                    String tempText = comment.getText();
                    if (tempText.equals(startNodeName)) {
                        catched = true;
                        continue;
                    }
                    if (tempText.equals(endNodeName)) {
                        catched = false;

                        continue;
                    }

                    rootout.addComment(tempText);
                }
            }

            OutputFormat format = new OutputFormat();
            format.setEncoding("UTF-8");
            format.setIndent(true);
            format.setNewlines(true);
            format.setLineSeparator("\t\n");
            format.setNewLineAfterDeclaration(true);


            File fileout = new File(fileDir);
            if (fileout.exists()) {
                fileout.delete();
            }

            fileout.createNewFile();
            XMLWriter out = new XMLWriter(new FileWriter(fileout), format);

            out.write(documentout);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void writeResultToDimens(String filename, String fileDir) {
        System.out.println("filePath" + fileDir);


        String intXML = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileDir));
            String line = null;
            while ((line = br.readLine()) != null) {
                intXML += line;
            }
            br.close();

            org.dom4j.Document document = DocumentHelper.parseText(intXML);
            Element root = document.getRootElement();
            org.dom4j.Document documentout = DocumentHelper.createDocument();
            Element rootout = documentout.addElement(root.getName());

            String startNodeName = filename + "_begin";
            String endNodeName = filename + "_end";

            boolean skip = false;
            for (int i = 0, size = root.nodeCount(); i < size; i++) {
                Node node = root.node(i);
                if (node instanceof Element) {
                    if (skip) continue;

                    Element element = (Element) node;
                    Element elementout = rootout.addElement(element.getName());
                    elementout.addAttribute("name", element.attributeValue("name"));
                    elementout.addText(element.getText());
                } else if (node instanceof Comment) {
                    Comment comment = (Comment) node;

                    String tempText = comment.getText();
                    if (tempText.equals(startNodeName)) {
                        skip = true;
                        continue;
                    }
                    if (tempText.equals(endNodeName)) {
                        skip = false;
                        continue;
                    }
                    rootout.addComment(tempText);

                }
            }

            Set<String> keyset = dimensMap.keySet();
            Iterator<String> setinterator = keyset.iterator();


            rootout.addComment(startNodeName);
            while (setinterator.hasNext()) {
                String key = setinterator.next();
                String value = dimensMap.get(key);
                Element elementout = rootout.addElement("dimen");
                elementout.addAttribute("name", key);
                elementout.addText(value);
            }
            rootout.addComment(endNodeName);


            OutputFormat format = new OutputFormat();
            format.setEncoding("UTF-8");
            format.setIndent(true);
            format.setNewlines(true);
            format.setLineSeparator("\t\n");
            format.setNewLineAfterDeclaration(true);


            File fileout = new File(fileDir);
            if (fileout.exists()) {
                fileout.delete();
            }

            fileout.createNewFile();
            XMLWriter out = new XMLWriter(new FileWriter(fileout), format);

            out.write(documentout);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void addAttrByText(Element elementout, Attribute attribute, String text) {
        if (!attribute.getNamespacePrefix().equals("")) {
            elementout.addAttribute(attribute.getNamespacePrefix() + ":" + attribute.getName(), text);
        } else {
            elementout.addAttribute(attribute.getName(), text);
        }
    }

    /**
     * 生产index
     *
     * @param oldIndex
     * @param index
     * @return simple 1_1
     */
    private String getIndex(String oldIndex, int index) {
        StringBuffer sb = new StringBuffer();
        if (oldIndex != null) {
            sb.append(oldIndex);
            sb.append("_");
        }
        sb.append(index);
        return sb.toString();
    }

    /**
     * @param element   要处理的元素
     * @param attribute 要处理的元素属性
     * @param index     元素在xml的位子
     * @return
     */
    private String getDimensName(Element element, Attribute attribute, String index) {

        Attribute attributeId = element.attribute("id");
        StringBuffer sb = new StringBuffer();
        if (attributeId != null) {
            //attributeId: @+id/course_level_item3
            //course_level_item3_w
//            System.out.println("attributeId.getText():" + attributeId.getText());
            String[] idstringarrr = attributeId.getText().split("/");
            String id = idstringarrr[1];
            sb.append(id);
        } else {
            //filename_3_3_w
            sb.append(donutmentFilename);
            sb.append("_");
            sb.append(index);
        }
        String attr = "_" + attribute.getName();
        if (attribute.getName().equals("layout_width")) {
            attr = "_w";
        } else if (attribute.getName().equals("layout_height")) {
            attr = "_h";
        } else if (attribute.getName().equals("layout_marginLeft")) {
            attr = "_ml";
        } else if (attribute.getName().equals("layout_marginRight")) {
            attr = "_mr";
        } else if (attribute.getName().equals("layout_marginTop")) {
            attr = "_mt";
        } else if (attribute.getName().equals("layout_marginBottom")) {
            attr = "_mb";
        } else if (attribute.getName().equals("layout_marginEnd")) {
            attr = "_me";
        } else if (attribute.getName().equals("layout_marginStart")) {
            attr = "_ms";
        } else if (attribute.getName().equals("layout_margin")) {
            attr = "_m";
        } else if (attribute.getName().equals("padding")) {
            attr = "_p";
        } else if (attribute.getName().equals("paddingLeft")) {
            attr = "_pl";
        } else if (attribute.getName().equals("paddingRight")) {
            attr = "_pr";
        } else if (attribute.getName().equals("paddingTop")) {
            attr = "_pt";
        } else if (attribute.getName().equals("paddingBottom")) {
            attr = "_pb";
        } else if (attribute.getName().equals("paddingEnd")) {
            attr = "_pe";
        } else if (attribute.getName().equals("paddingStart")) {
            attr = "_ps";
        } else if (attribute.getName().equals("textSize")) {
            attr = "_ts";
        }
        sb.append(attr);
        return sb.toString().toLowerCase();

    }

    private void doWriteDimensElement(String rootindex, Element inroot, Element outroot) {

        for (int i = 0, size = inroot.nodeCount(); i < size; i++) {
            Node node = inroot.node(i);
            if (node instanceof Element) {
                Element element = (Element) node;

                String elementName = element.getName();
                Element elementout = outroot.addElement(elementName);
                List<Attribute> attributes = element.attributes();
                String index = getIndex(rootindex, i + 1);
                for (Attribute attribute : attributes) {
                    String text = attribute.getText();

                    if (text.endsWith("px") || text.endsWith("dp")) {
                        String dimensName = getDimensName(element, attribute, index);
                        String newText = "@dimen/" + dimensName;
                        dimensMap.put(dimensName, text);
                        addAttrByText(elementout, attribute, newText);
                    } else {
                        addAttr(elementout, attribute);
                    }

                }
                if (element.elements().size() > 0) {
                    doWriteDimensElement(index, element, elementout);
                }

            }

        }

    }


    //------------------------------------------   write to Dimens ------------------------------------------------------//


    public void changePx2Percent(int screenw, int screenh, boolean percentW) {


        try {
            com.intellij.openapi.editor.Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            Document document = editor.getDocument();


            VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            String filename = file.getName();
            if (!filename.endsWith(".xml")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid XML files", NotificationType.INFORMATION));

                return;
            }
            System.out.println("file name = " + file.getName());

            org.dom4j.Document documentxml = DocumentHelper.parseText(document.getText());
            Element root = documentxml.getRootElement();
            String rootname = root.getName();
            if (!rootname.endsWith("Layout")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid XML files", NotificationType.INFORMATION));
                return;
            }


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

                    Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "changePx2Percent complete", NotificationType.INFORMATION));
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
            if (!filename.endsWith(".xml")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid XML files", NotificationType.INFORMATION));
                return;
            }

            org.dom4j.Document documentxml = DocumentHelper.parseText(document.getText());
            Element root = documentxml.getRootElement();
            String rootname = root.getName();
            if (!rootname.endsWith("Layout")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid Layout XML files", NotificationType.INFORMATION));

                return;
            }
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
                    Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "cleanPercent complete", NotificationType.INFORMATION));

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
            if (!filename.endsWith(".xml")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid XML files", NotificationType.INFORMATION));

                return;
            }

            org.dom4j.Document documentin = DocumentHelper.parseText(document.getText());
            org.dom4j.Document documentout = DocumentHelper.createDocument();

            Element root = documentin.getRootElement();
            String rootname = root.getName();
            if (!rootname.equals("resources")) {
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "Not valid dimens XML files", NotificationType.INFORMATION));
                return;
            }

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
                Notifications.Bus.notify(new com.intellij.notification.Notification("android px2dp", "px2dp info", "dimensChange complete", NotificationType.INFORMATION));


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
