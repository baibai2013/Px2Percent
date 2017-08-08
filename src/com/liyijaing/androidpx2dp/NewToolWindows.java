package com.liyijaing.androidpx2dp;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class NewToolWindows implements ToolWindowFactory {


    private JPanel myToolWindowContent;
    private JComboBox jcb_screenOrientation;
    private JComboBox jcb_screenWH;
    private JComboBox jcb_dependsWOrH;
    private JComboBox jcb_wstype;
    private JButton changebtn;
    private JButton canclebtn;
    private JButton pxtodpbtn;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JCheckBox checkBox4;
    private JCheckBox checkBox5;
    private JCheckBox checkBox6;
    private JCheckBox checkBox7;
    private JCheckBox checkBox8;
    private JCheckBox checkBox9;
    private JCheckBox checkBox10;
    private JCheckBox checkBox11;
    private JCheckBox checkBox12;

    public static final String COMMOND_CHANGE = "commond_change";
    public static final String COMMOND_CANCLE = "commond_cancle";
    public static final String COMMOND_PX2DP = "commond_px2dp";

    private ArrayList<JCheckBox> jCheckBoxes;
    String[] string_table_title;//= {"key", "value"};
    String[] string_screenwh;// = {"1920x1080", "2048x1536", "1280x720"};
    String[] string_screenOrientation;//= {"portrait", "landscape"};
    String[] string_screendepend;// = {"width", "height"};
    String[] string_device;// = {"value-sw phone", "value-sw pad", "value-sw roobo"};
    int[] int_sw_base;// = {360, 768, 480};
    float[] int_sw_scale;// = {3, 2, 1.5f};
    String[] string_swkey;//= {"value-sw320dp", "value-sw360dp", "value-sw400dp", "value-sw432dp", "value-sw480dp", "value-sw500dp", "value-sw533dp", "value-sw580dp", "value-sw600dp", "value-sw680dp", "value-sw720dp", "value-sw768dp"};
    int[] string_swphone;// = {1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1};
    int[] string_swpad;//= {1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1};
    int[] string_swroobo;//= {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
    int[] int_swvalue;

    AndroidPx2DpControl androidPx2DpControl;
    Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        System.out.println("createToolWindowContent");
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);

        this.project = project;
        androidPx2DpControl = new AndroidPx2DpControl(project);


        initDate();
    }

    @Override
    public void init(ToolWindow window) {

        System.out.println("init");

    }


    private void initDate(){

        jCheckBoxes = new ArrayList<JCheckBox>();
        int_swvalue = new int[]{320, 360, 400, 432, 480, 500, 533, 580, 600, 680, 720, 768};
        string_swroobo = new int[]{0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
        string_swpad = new int[]{1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1};
        string_table_title = new String[]{"key", "value"};
        string_screenwh = new String[]{"1920x1080", "2048x1536", "1280x720"};
        string_screenOrientation = new String[]{"portrait", "landscape"};
        string_screendepend = new String[]{"width", "height"};
        string_device = new String[]{"value-sw phone", "value-sw pad", "value-sw roobo"};
        int_sw_base = new int[]{360, 768, 480};
        int_sw_scale = new float[]{3, 2, 1.5f};
        string_swkey = new String[]{"value-sw320dp", "value-sw360dp", "value-sw400dp", "value-sw432dp", "value-sw480dp", "value-sw500dp", "value-sw533dp", "value-sw580dp", "value-sw600dp", "value-sw680dp", "value-sw720dp", "value-sw768dp"};
        string_swphone = new int[]{1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1};


        DefaultComboBoxModel defaultComboBoxModel_screenOrientation = new DefaultComboBoxModel(string_screenOrientation);
        jcb_screenOrientation.setModel(defaultComboBoxModel_screenOrientation);

        DefaultComboBoxModel defaultComboBoxModel_screenwh = new DefaultComboBoxModel(string_screenwh);
        jcb_screenWH.setModel(defaultComboBoxModel_screenwh);

        DefaultComboBoxModel defaultComboBoxModel_string_screendepend = new DefaultComboBoxModel(string_screendepend);
        jcb_dependsWOrH.setModel(defaultComboBoxModel_string_screendepend);


        DefaultComboBoxModel defaultComboBoxModel_string_wsplatform = new DefaultComboBoxModel(string_device);
        jcb_wstype.setModel(defaultComboBoxModel_string_wsplatform);


        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if (command.equals(COMMOND_CHANGE)) {
                    System.out.println("change click");


                    int screenOrientationindex = jcb_screenOrientation.getSelectedIndex();
                    int widthOrHeightindex = jcb_screenWH.getSelectedIndex();
                    int screendependeindex = jcb_dependsWOrH.getSelectedIndex();
                    String string_screenWH = string_screenwh[widthOrHeightindex];
                    String[] arr_screenWH = string_screenWH.split("x");
                    int screenW, screenH;
                    boolean percent_w;
                    if (screenOrientationindex == 0) {
                        screenW = Integer.valueOf(arr_screenWH[1]);
                        screenH = Integer.valueOf(arr_screenWH[0]);
                    } else {
                        screenW = Integer.valueOf(arr_screenWH[0]);
                        screenH = Integer.valueOf(arr_screenWH[1]);
                    }

                    if (screendependeindex == 0) {
                        percent_w = true;
                    } else {
                        percent_w = false;
                    }


                    System.out.println("screenW:" + screenW + "--screenH:" + screenH + "--percent_w:" + percent_w);
                    androidPx2DpControl.changePx2Percent(screenW, screenH, percent_w);


                } else if (command.equals(COMMOND_CANCLE)) {
                    System.out.println("cancle click");

                    androidPx2DpControl.cleanPercent();

                } else if (command.equals(COMMOND_PX2DP)) {
                    System.out.println("px2dp click");


                    int deviceType = jcb_wstype.getSelectedIndex();
                    ArrayList<Integer> platforms = new ArrayList<Integer>();
                    for (int i = 0; i < jCheckBoxes.size(); i++) {
                        if (jCheckBoxes.get(i).isSelected()) {
                            Integer plaform = new Integer(int_swvalue[i]);
//                    System.out.println("int_swvalue[i]:"+plaform.intValue());
                            platforms.add(plaform);
                        }
                    }

                    int swbase = int_sw_base[deviceType];
                    float swscale = int_sw_scale[deviceType];


                    androidPx2DpControl.dimensChange(swscale, swbase, int_swvalue, platforms);


                }
            }
        };


        changebtn.setActionCommand(COMMOND_CHANGE);
        changebtn.addActionListener(actionListener);

        canclebtn.setActionCommand(COMMOND_CANCLE);
        canclebtn.addActionListener(actionListener);

        pxtodpbtn.setActionCommand(COMMOND_PX2DP);
        pxtodpbtn.addActionListener(actionListener);


        jCheckBoxes.add(checkBox1);
        jCheckBoxes.add(checkBox2);
        jCheckBoxes.add(checkBox3);
        jCheckBoxes.add(checkBox4);
        jCheckBoxes.add(checkBox5);
        jCheckBoxes.add(checkBox6);
        jCheckBoxes.add(checkBox7);
        jCheckBoxes.add(checkBox8);
        jCheckBoxes.add(checkBox9);
        jCheckBoxes.add(checkBox10);
        jCheckBoxes.add(checkBox11);
        jCheckBoxes.add(checkBox12);


        jcb_wstype.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    int index = jcb_wstype.getSelectedIndex();
                    switch (index) {
                        case 0:
                            setswValue(string_swphone);
                            break;
                        case 1:
                            setswValue(string_swpad);
                            break;
                        case 2:
                            setswValue(string_swroobo);
                            break;

                    }
                }


            }
        });

        setswValue(string_swphone);

    }


    private void setswValue(int[] string_swphone) {
        for (int i = 0; i < jCheckBoxes.size(); i++) {
            JCheckBox jCheckBox = jCheckBoxes.get(i);
            int state = string_swphone[i];
            if (state == 1) {
                jCheckBox.setSelected(true);
            } else if (state == 0) {
                jCheckBox.setSelected(false);
            }
        }
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }
}
