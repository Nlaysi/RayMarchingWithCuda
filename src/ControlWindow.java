import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlWindow extends JFrame {
    JButton add, delete, refresh;
    ActionListener actionListener = new Actions();

    public String[] elements = new String[] {"Sphere", "Cube",
            "Cylinder", "Triangular Prism"};
    public String[] elements2 = new String[] {"∨", "∧", "-"};

    private JComboBox comboFigure = new JComboBox(elements);
    private JComboBox comboType = new JComboBox(elements2);
    private JList list = new JList(Object.getList());
    private JTextField fieldR, fieldG, fieldB;
    private JTextField v1x, v1y, v1z;
    private JTextField v2x, v2y, v2z;
    private JTextField r;

    JLabel label = new JLabel("                     ");
    JLabel labelC = new JLabel("Color: ");
    JLabel labelV1 = new JLabel("Vector 1: ");
    JLabel labelV2 = new JLabel("Vector 2: ");
    JLabel labelR = new JLabel("R: ");

    public ControlWindow(String str){
        super(str);
        setLayout(new FlowLayout());

        add = new JButton("Add");
        delete = new JButton("Delete");
        refresh = new JButton("Refresh");
        fieldR = new JTextField(3);
        fieldG = new JTextField(3);
        fieldB = new JTextField(3);
        v1x = new JTextField(3);
        v1y = new JTextField(3);
        v1z = new JTextField(3);
        v2x = new JTextField(3);
        v2y = new JTextField(3);
        v2z = new JTextField(3);
        r = new JTextField(3);

        add(add);
        add.addActionListener(actionListener);
        add(delete);
        delete.addActionListener(actionListener);
        add(refresh);
        comboFigure.addActionListener(actionListener);
        add(comboFigure);
        add(comboType);
        add(labelC);
        add(fieldR);
        add(fieldG);
        add(fieldB);
        add(labelV1);
        add(v1x);
        add(v1y);
        add(v1z);
        add(label);
        add(labelV2);
        add(v2x);
        add(v2y);
        add(v2z);
        add(labelR);
        add(r);
        //list.setFont(new Font("Courier New", Font.PLAIN, 10));
        add(list);

        setEnabled(true,true,true,true,true,true,false,false,false,true);
    }

    private void setEnabled(boolean R, boolean G, boolean B, boolean v1x, boolean v1y, boolean v1z, boolean v2x, boolean v2y, boolean v2z, boolean r){
        this.fieldR.setEnabled(R);
        this.fieldG.setEnabled(G);
        this.fieldB.setEnabled(B);
        this.v1x.setEnabled(v1x);
        this.v1y.setEnabled(v1y);
        this.v1z.setEnabled(v1z);
        this.v2x.setEnabled(v2x);
        this.v2y.setEnabled(v2y);
        this.v2z.setEnabled(v2z);
        this.r.setEnabled(r);
    }

    public class Actions implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getSource() == add) {
                    if (comboFigure.getSelectedIndex() == 0) {
                        new Object(
                                (byte) (1 + comboType.getSelectedIndex()),
                                Byte.parseByte(fieldR.getText()),
                                Byte.parseByte(fieldR.getText()),
                                Byte.parseByte(fieldR.getText()),
                                new Vector(
                                        Double.parseDouble(v1x.getText()),
                                        Double.parseDouble(v1y.getText()),
                                        Double.parseDouble(v1z.getText())),
                                new Vector(0, 0, 0),
                                Double.parseDouble(r.getText()));
                    }
                    if (comboFigure.getSelectedIndex() == 1) {
                        new Object(
                                (byte) ((comboFigure.getSelectedIndex())*3 + 1 + comboType.getSelectedIndex()),
                                Byte.parseByte(fieldR.getText()),
                                Byte.parseByte(fieldG.getText()),
                                Byte.parseByte(fieldB.getText()),
                                new Vector(
                                        Double.parseDouble(v1x.getText()),
                                        Double.parseDouble(v1y.getText()),
                                        Double.parseDouble(v1z.getText())),
                                new Vector(
                                        Double.parseDouble(v2x.getText()),
                                        Double.parseDouble(v2y.getText()),
                                        Double.parseDouble(v2z.getText())),
                                0.0);
                        System.out.println((comboFigure.getSelectedIndex())*3 + 1 + comboType.getSelectedIndex());
                    }
                    if (comboFigure.getSelectedIndex() == 2) {
                        new Object(
                                (byte) ((comboFigure.getSelectedIndex())*3 + 1 + comboType.getSelectedIndex()),
                                Byte.parseByte(fieldR.getText()),
                                Byte.parseByte(fieldG.getText()),
                                Byte.parseByte(fieldB.getText()),
                                new Vector(
                                        Double.parseDouble(v1x.getText()),
                                        Double.parseDouble(v1y.getText()),
                                        Double.parseDouble(v1z.getText())),
                                new Vector(
                                        Double.parseDouble(v2x.getText()),
                                        Double.parseDouble(v2y.getText()),
                                        Double.parseDouble(v2z.getText())),
                                Double.parseDouble(r.getText()));
                    }
                    if (comboFigure.getSelectedIndex() == 3) {
                        new Object(
                                (byte) ((comboFigure.getSelectedIndex())*3 + 1 + comboType.getSelectedIndex()),
                                Byte.parseByte(fieldR.getText()),
                                Byte.parseByte(fieldR.getText()),
                                Byte.parseByte(fieldR.getText()),
                                new Vector(
                                        Double.parseDouble(v1x.getText()),
                                        Double.parseDouble(v1y.getText()),
                                        Double.parseDouble(v1z.getText())),
                                new Vector(0, 0, 0),
                                Double.parseDouble(r.getText()));
                    }

                    Object.LoadObjects();
                    list.setListData(Object.getList());
                }

                if (e.getSource() == refresh) {
                    Object.LoadObjects();
                }

                if (comboFigure.getSelectedIndex() == 0) {
                    setEnabled(true, true, true, true, true, true, false, false, false, true);
                }
                if (comboFigure.getSelectedIndex() == 1) {
                    setEnabled(true, true, true, true, true, true, true, true, true, false);
                }
                if (comboFigure.getSelectedIndex() == 2) {
                    setEnabled(true, true, true, true, true, true, true, true, true, true);
                }
                if (comboFigure.getSelectedIndex() == 3) {
                    setEnabled(true, true, true, true, true, true, false, false, false, true);
                }


                if (e.getSource() == delete) {
                    Object.deleteElement(list.getSelectedIndex());
                    Object.LoadObjects();
                    list.setListData(Object.getList());
                }
            }
            catch (Exception err){
                System.err.println(err.toString());
            }
        }
    }
}
