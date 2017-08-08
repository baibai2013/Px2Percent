//package com.liyijaing.androidpx2dp;
//
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.ui.ComboBox;
//import com.intellij.openapi.wm.ToolWindow;
//import com.intellij.openapi.wm.ToolWindowFactory;
//import com.intellij.ui.components.JBScrollPane;
//import com.intellij.ui.content.Content;
//import com.intellij.ui.content.ContentFactory;
//import com.intellij.util.ui.CheckBox;
//import org.jetbrains.annotations.NotNull;
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.util.ArrayList;
//
//public class MyToolWindows implements ToolWindowFactory {
//
//
////    public static final String COMMOND_CHANGE = "commond_change";
////    public static final String COMMOND_CANCLE = "commond_cancle";
////    public static final String COMMOND_PX2DP = "commond_px2dp";
//
//    String[] string_table_title;//= {"key", "value"};
//    String[] string_screenwh;// = {"1920x1080", "2048x1536", "1280x720"};
//    String[] string_screenOrientation;//= {"portrait", "landscape"};
//    String[] string_screendepend;// = {"width", "height"};
//    String[] string_device;// = {"value-sw phone", "value-sw pad", "value-sw roobo"};
//    int[] int_sw_base;// = {360, 768, 480};
//    float[] int_sw_scale;// = {3, 2, 1.5f};
//    String[] string_swkey;//= {"value-sw320dp", "value-sw360dp", "value-sw400dp", "value-sw432dp", "value-sw480dp", "value-sw500dp", "value-sw533dp", "value-sw580dp", "value-sw600dp", "value-sw680dp", "value-sw720dp", "value-sw768dp"};
//    int[] string_swphone;// = {1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1};
//    int[] string_swpad;//= {1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1};
//    int[] string_swroobo;//= {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
//    int[] int_swvalue;
//
//
//    private JPanel myToolWindowContent;
//
//    private JComboBox jcb_screenWH;
//    private JComboBox jcb_screenOrientation;
//    private JComboBox jcb_dependsWOrH;
//    private JComboBox jcb_wstype;
//
//    AndroidPx2DpControl androidPx2DpControl;
//
//
//    private JButton changebtn;
//    private JButton pxtodpbtn;
//    private JButton canclebtn;
//
//    private JCheckBox jc_sw_320;
//    private JCheckBox jc_sw_360;
//    private JCheckBox jc_sw_400;
//    private JCheckBox jc_sw_432;
//    private JCheckBox jc_sw_480;
//    private JCheckBox jc_sw_500;
//    private JCheckBox jc_sw_533;
//    private JCheckBox jc_sw_580;
//    private JCheckBox jc_sw_600;
//    private JCheckBox jc_sw_680;
//    private JCheckBox jc_sw_720;
//    private JCheckBox jc_sw_780;
//    private JPanel j1;
//    private JPanel j2;
//    private JPanel j0;
//    private JLabel l1;
//    private JLabel l2;
//    private JLabel l3;
//    private JLabel l4;
//    private JLabel l5;
//    private JLabel l6;
//    private JLabel l7;
//    private JLabel l8;
//    private JLabel l9;
//    private JLabel l10;
//    private JLabel l11;
//    private JLabel l12;
//    private JLabel l13;
//    private JLabel l14;
//    private JLabel l15;
//    private JLabel l16;
//    private JLabel l17;
//    private JLabel l18;
//
//    Project project;
//
//    private ArrayList<JCheckBox> jCheckBoxes;
//
//    public MyToolWindows() {
//
//    }
//
//    @Override
//    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
//        this.project = project;
//        System.out.println("createToolWindowContent");
//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        Content content = contentFactory.createContent(myToolWindowContent, "", false);
//        toolWindow.getContentManager().addContent(content);
//
////        androidPx2DpControl = new AndroidPx2DpControl(project);
//
//
////
////        DefaultComboBoxModel defaultComboBoxModel_screenOrientation = new DefaultComboBoxModel(string_screenOrientation);
//////        jcb_screenOrientation = new ComboBox(defaultComboBoxModel_screenOrientation);
////        jcb_screenOrientation.setModel(defaultComboBoxModel_screenOrientation);
////
//////        JLabel screenWHLb = new JLabel("Screen:");
////        DefaultComboBoxModel defaultComboBoxModel_screenwh = new DefaultComboBoxModel(string_screenwh);
//////        jcb_screenWH = new ComboBox(defaultComboBoxModel_screenwh);
////        jcb_screenWH.setModel(defaultComboBoxModel_screenwh);
////
//////        JLabel screendependLb =defaultComboBoxModel_string_screendepend new JLabel("Depend:");
////        DefaultComboBoxModel defaultComboBoxModel_string_screendepend = new DefaultComboBoxModel(string_screendepend);
//////        jcb_dependsWOrH = new ComboBox(defaultComboBoxModel_string_screendepend);
////        jcb_dependsWOrH.setModel(defaultComboBoxModel_string_screendepend);
////
////
////        DefaultComboBoxModel defaultComboBoxModel_string_wsplatform = new DefaultComboBoxModel(string_device);
//////        jcb_wstype = new ComboBox(defaultComboBoxModel_string_wsplatform);
////        jcb_wstype.setModel(defaultComboBoxModel_string_wsplatform);
////
//////        JButton changeBtn = new JButton("  Change to Percent ");
//////        JButton cancleBtn = new JButton("    Clear Percent   ");
//////        JButton pxtoDpBtn = new JButton("        Px to Dp          ");
////
////
//////        JLabel wsvaluesLb = new JLabel("values:");
//////        for (int i = 0; i < string_swkey.length; i++) {
//////            String title = string_swkey[i];
//////            JCheckBox checkBox = new JCheckBox(title,false);
//////            jCheckBoxes.add(checkBox);
//////        }
////
////        jCheckBoxes.add(jc_sw_320);
////        jCheckBoxes.add(jc_sw_360);
////        jCheckBoxes.add(jc_sw_400);
////        jCheckBoxes.add(jc_sw_432);
////        jCheckBoxes.add(jc_sw_480);
////        jCheckBoxes.add(jc_sw_500);
////        jCheckBoxes.add(jc_sw_533);
////        jCheckBoxes.add(jc_sw_580);
////        jCheckBoxes.add(jc_sw_600);
////        jCheckBoxes.add(jc_sw_680);
////        jCheckBoxes.add(jc_sw_720);
////        jCheckBoxes.add(jc_sw_780);
////
////
////        ActionListener actionListener = new ActionListener() {
////            @Override
////            public void actionPerformed(ActionEvent e) {
////                String command = e.getActionCommand();
////                if (command.equals(COMMOND_CHANGE)) {
////                    System.out.println("change click");
////
////
////                    int screenOrientationindex = jcb_screenOrientation.getSelectedIndex();
////                    int widthOrHeightindex = jcb_screenWH.getSelectedIndex();
////                    int screendependeindex = jcb_dependsWOrH.getSelectedIndex();
////                    String string_screenWH = string_screenwh[widthOrHeightindex];
////                    String[] arr_screenWH = string_screenWH.split("x");
////                    int screenW, screenH;
////                    boolean percent_w;
////                    if (screenOrientationindex == 0) {
////                        screenW = Integer.valueOf(arr_screenWH[1]);
////                        screenH = Integer.valueOf(arr_screenWH[0]);
////                    } else {
////                        screenW = Integer.valueOf(arr_screenWH[0]);
////                        screenH = Integer.valueOf(arr_screenWH[1]);
////                    }
////
////                    if (screendependeindex == 0) {
////                        percent_w = true;
////                    } else {
////                        percent_w = false;
////                    }
////
////
////                    System.out.println("screenW:" + screenW + "--screenH:" + screenH + "--percent_w:" + percent_w);
////                    androidPx2DpControl.changePx2Percent(screenW, screenH, percent_w);
////
////
////                } else if (command.equals(COMMOND_CANCLE)) {
////                    System.out.println("cancle click");
////
////                    androidPx2DpControl.cleanPercent();
////
////                } else if (command.equals(COMMOND_PX2DP)) {
////                    System.out.println("px2dp click");
////
////
////                    int deviceType = jcb_wstype.getSelectedIndex();
////                    ArrayList<Integer> platforms = new ArrayList<Integer>();
////                    for (int i = 0; i < jCheckBoxes.size(); i++) {
////                        if (jCheckBoxes.get(i).isSelected()) {
////                            Integer plaform = new Integer(int_swvalue[i]);
//////                    System.out.println("int_swvalue[i]:"+plaform.intValue());
////                            platforms.add(plaform);
////                        }
////                    }
////
////                    int swbase = int_sw_base[deviceType];
////                    float swscale = int_sw_scale[deviceType];
////
////
////                    androidPx2DpControl.dimensChange(swscale, swbase, int_swvalue, platforms);
////
////
////                }
////            }
////        };
////
////
////        changebtn.setActionCommand(COMMOND_CHANGE);
////        changebtn.addActionListener(actionListener);
////
////        canclebtn.setActionCommand(COMMOND_CANCLE);
////        canclebtn.addActionListener(actionListener);
////
////        pxtodpbtn.setActionCommand(COMMOND_PX2DP);
////        pxtodpbtn.addActionListener(actionListener);
////
////        jcb_wstype.addItemListener(new ItemListener() {
////            @Override
////            public void itemStateChanged(ItemEvent e) {
////                if (e.getStateChange() == ItemEvent.SELECTED) {
////                    int index = jcb_wstype.getSelectedIndex();
////                    switch (index) {
////                        case 0:
////                            setswValue(string_swphone);
////                            break;
////                        case 1:
////                            setswValue(string_swpad);
////                            break;
////                        case 2:
////                            setswValue(string_swroobo);
////                            break;
////
////                    }
////                }
////
////
////            }
////        });
////        setswValue(string_swphone);
//
//
//    }
//
//    @Override
//    public void init(ToolWindow window) {
//        System.out.println("init");
////        jCheckBoxes = new ArrayList<JCheckBox>();
////        int_swvalue = new int[]{320, 360, 400, 432, 480, 500, 533, 580, 600, 680, 720, 768};
////        string_swroobo = new int[]{0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
////        string_swpad = new int[]{1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1};
////        string_table_title = new String[]{"key", "value"};
////        string_screenwh = new String[]{"1920x1080", "2048x1536", "1280x720"};
////        string_screenOrientation = new String[]{"portrait", "landscape"};
////        string_screendepend = new String[]{"width", "height"};
////        string_device = new String[]{"value-sw phone", "value-sw pad", "value-sw roobo"};
////        int_sw_base = new int[]{360, 768, 480};
////        int_sw_scale = new float[]{3, 2, 1.5f};
////        string_swkey = new String[]{"value-sw320dp", "value-sw360dp", "value-sw400dp", "value-sw432dp", "value-sw480dp", "value-sw500dp", "value-sw533dp", "value-sw580dp", "value-sw600dp", "value-sw680dp", "value-sw720dp", "value-sw768dp"};
////        string_swphone = new int[]{1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1};
//
//
//    }
//
//    private void setswValue(int[] string_swphone) {
//        for (int i = 0; i < jCheckBoxes.size(); i++) {
//            JCheckBox jCheckBox = jCheckBoxes.get(i);
//            int state = string_swphone[i];
//            if (state == 1) {
//                jCheckBox.setSelected(true);
//            } else if (state == 0) {
//                jCheckBox.setSelected(false);
//            }
//        }
//    }
//
//    @Override
//    public boolean shouldBeAvailable(@NotNull Project project) {
//        System.out.println("shouldBeAvailable");
//        return false;
//    }
//
//    @Override
//    public boolean isDoNotActivateOnStart() {
//        System.out.println("isDoNotActivateOnStart");
//        return false;
//    }
//
//    private void createUIComponents() {
//        // TODO: place custom component creation code here
//    }
//
//
//}
