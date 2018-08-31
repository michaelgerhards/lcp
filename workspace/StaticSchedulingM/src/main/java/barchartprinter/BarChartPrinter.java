package barchartprinter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Nithammer
 */
public class BarChartPrinter extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = -1828612359211692994L;
    private Map<MyString, double[]> dPoints;
    private Color[] colors;
    private double highestDValue;
    private static int errNr = 0;
    public static final double defaultEpsilon = 0.00000001;
    private static final boolean debug = false;
    public static String labelLegendTitle = "ID";
    private static String dataLegendTitle = "Time in sec.";
    private static String fontStyle = Font.MONOSPACED;
    public static boolean noTitle = false;

    public static void main(String... args) {
        // printBars("Double test with big epsilon",new double[]{1,0.2,6,9},new
        // double[]{2,5.6,10,14.3},new double[]{3.7,6.5412,10,15},new
        // String[]{"A","B","A","A"},1);
        // printBars("Double test with too small epsilon",new
        // double[]{1,0.2,6,9},new double[]{2,5.6,10,14.3},new
        // double[]{3.7,6.5412,10,15},new String[]{"A","B","A","A"});
        printBars("Error test", new double[]{107.02, 80.33, 147.99, 26.8,
            161.41000000000003, 134.42000000000002, 40.1, 188.27, 13.17,
            174.57000000000002, 120.86999999999999, 201.92000000000002,
            53.35, 66.97, 0.0, 93.44, 490.01, 766.6199999999999, 236.74,
            574.98, 416.25, 437.24, 310.76, 660.23, 628.3699999999999,
            289.66999999999996, 553.6499999999999, 670.97, 777.22, 363.35,
            617.46, 713.6299999999999, 840.5899999999999, 500.58, 638.98,
            521.84, 447.88, 596.26, 851.0799999999999, 226.16000000000003,
            300.25, 278.9, 395.22, 830.19, 426.70000000000005,
            819.6299999999999, 384.6, 342.29, 247.32, 798.3799999999999,
            458.55, 257.8, 564.3199999999999, 268.34999999999997,
            809.0699999999999, 321.34, 331.81, 374.09, 787.79,
            734.6699999999998, 724.1799999999998, 755.98,
            469.20000000000005, 479.66, 585.6199999999999,
            745.3899999999999, 649.51, 405.68, 703.02, 606.8,
            681.6399999999999, 215.73000000000002, 542.8599999999999,
            511.19, 532.3699999999999, 692.4099999999999, 352.88, 861.49,
            872.1399999999999, 877.0999999999999, 904.3, 882.44, 1000.78,
            936.7899999999997, 958.2099999999998, 979.5999999999999,
            893.4000000000001, 926.0399999999997, 1022.1299999999999,
            915.1799999999998, 1011.3899999999999, 947.5199999999998,
            1043.6299999999997, 990.0899999999999, 968.8699999999999,
            1032.8499999999997, 1054.3699999999997, 1061.9999999999995,
            1071.5999999999997, 1078.5099999999998}, new double[]{
            120.86999999999999, 93.44, 161.41000000000003, 40.1,
            174.57000000000002, 147.99, 53.35, 201.92000000000002, 26.8,
            188.27, 134.42000000000002, 215.73000000000002, 66.97, 80.33,
            13.17, 107.02, 500.58, 777.22, 247.32, 585.6199999999999,
            426.70000000000005, 447.88, 321.34, 670.97, 638.98, 300.25,
            564.3199999999999, 681.6399999999999, 787.79, 374.09,
            628.3699999999999, 724.1799999999998, 851.0799999999999,
            511.19, 649.51, 532.3699999999999, 458.55, 606.8, 861.49,
            236.74, 310.76, 289.66999999999996, 405.68, 840.5899999999999,
            437.24, 830.19, 395.22, 352.88, 257.8, 809.0699999999999,
            469.20000000000005, 268.34999999999997, 574.98, 278.9,
            819.6299999999999, 331.81, 342.29, 384.6, 798.3799999999999,
            745.3899999999999, 734.6699999999998, 766.6199999999999,
            479.66, 490.01, 596.26, 755.98, 660.23, 416.25,
            713.6299999999999, 617.46, 692.4099999999999,
            226.16000000000003, 553.6499999999999, 521.84,
            542.8599999999999, 703.02, 363.35, 872.1399999999999,
            877.0999999999999, 882.44, 915.1799999999998,
            893.4000000000001, 1011.3899999999999, 947.5199999999998,
            968.8699999999999, 990.0899999999999, 904.3, 936.7899999999997,
            1032.8499999999997, 926.0399999999997, 1022.1299999999999,
            958.2099999999998, 1054.3699999999997, 1000.78,
            979.5999999999999, 1043.6299999999997, 1061.9999999999995,
            1071.5999999999997, 1078.5099999999998, 1079.3399999999997},
                new double[]{120.86999999999999, 93.44, 161.41000000000003,
                    40.1, 174.57000000000002, 147.99, 53.35,
                    201.92000000000002, 26.8, 188.27, 134.42000000000002,
                    215.73000000000002, 66.97, 80.33, 13.17, 107.02,
                    500.58, 777.22, 247.32, 585.6199999999999,
                    426.70000000000005, 447.88, 321.34, 670.97, 638.98,
                    300.25, 564.3199999999999, 681.6399999999999, 787.79,
                    374.09, 628.3699999999999, 724.1799999999998,
                    851.0799999999999, 511.19, 649.51, 532.3699999999999,
                    458.55, 606.8, 861.49, 236.74, 310.76,
                    289.66999999999996, 405.68, 840.5899999999999, 437.24,
                    830.19, 395.22, 352.88, 257.8, 809.0699999999999,
                    469.20000000000005, 268.34999999999997, 574.98, 278.9,
                    819.6299999999999, 331.81, 342.29, 384.6,
                    798.3799999999999, 745.3899999999999,
                    734.6699999999998, 766.6199999999999, 479.66, 490.01,
                    596.26, 755.98, 660.23, 416.25, 713.6299999999999,
                    617.46, 692.4099999999999, 226.16000000000003,
                    553.6499999999999, 521.84, 542.8599999999999, 703.02,
                    363.35, 872.1399999999999, 877.0999999999999, 882.44,
                    915.1799999999998, 893.4000000000001,
                    1011.3899999999999, 947.5199999999998,
                    968.8699999999999, 990.0899999999999, 904.3,
                    936.7899999999997, 1032.8499999999997,
                    926.0399999999997, 1022.1299999999999,
                    958.2099999999998, 1054.3699999999997, 1000.78,
                    979.5999999999999, 1043.6299999999997,
                    1061.9999999999995, 1071.5999999999997,
                    1078.5099999999998, 1080.0},
                new String[]{"A", "12", "2", "13", "21", "1", "3", "4", "22",
                    "23", "23", "A", "A", "A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A",
                    "A", "A", "A", "A"}, 2);
    }

    public static JDialog printBars(String title, double[] startBlue,
            double[] endBlue, double[] endGreen, String[] label,
            double epsilon, Color[] colors) {
        final BarChartPrinter test = new BarChartPrinter(title, startBlue,
                endBlue, endGreen, label, epsilon, colors);
        // test.setLabelLegendTitle("test Label titel");
        // test.createDoubleGraph(title);
        test.pack();
        test.setVisible(true);
        return test;
    }

    public static void printBars(String title, List<Double> startBlue,
            List<Double> endBlue, List<Double> endGreen, List<String> label,
            double epsilon, List<Color> colors) {
        String[] s = new String[label.size()];
        Color[] c = new Color[endBlue.size()];
        double[] startB = new double[startBlue.size()];
        double[] endB = new double[endBlue.size()];
        double[] endG = new double[endGreen.size()];
        for (int i = 0; i < startB.length; i++) {
            startB[i] = startBlue.get(i);
            endB[i] = endBlue.get(i);
            endG[i] = endGreen.get(i);
        }
        printBars(title, startB, endB, endG, label.toArray(s), epsilon,
                colors.toArray(c));
    }

    public static JDialog printBars(String title, double[] startBlue,
            double[] endBlue, double[] endGreen, String[] label, double epsilon) {
        return printBars(title, startBlue, endBlue, endGreen, label, epsilon,
                new Color[label.length]);
    }

    public static void printBars(String title, List<Double> startBlue,
            List<Double> endBlue, List<Double> endGreen, List<String> label,
            double epsilon) {
        printBars(title, startBlue, endBlue, endGreen, label, epsilon,
                new ArrayList<Color>());
    }

    public static JDialog printBars(String title, double[] startBlue,
            double[] endBlue, double[] endGreen, String[] label) {
        return printBars(title, startBlue, endBlue, endGreen, label,
                defaultEpsilon);
    }

    public static void printBars(String title, List<Double> startBlue,
            List<Double> endBlue, List<Double> endGreen, List<String> label) {
        printBars(title, startBlue, endBlue, endGreen, label, defaultEpsilon);
    }

    public void setLabelLegendTitle(String newTitle) {
        labelLegendTitle = newTitle;
    }

    public void setDataLegendTitle(String newTitle) {
        dataLegendTitle = newTitle;
    }

    public BarChartPrinter(String title, double[] startBlue, double[] endBlue,
            double[] endGreen, String[] label, double epsilon, Color[] color) {
        dPoints = new TreeMap<MyString, double[]>();
        colors = new Color[label.length];
        System.arraycopy(color, 0, this.colors, 0, color.length);
        if (debug) {
            printData(title, startBlue, endBlue, endGreen, label, epsilon,
                    color);
        }
        for (int i = 0; i < label.length; i++) {
            if (endGreen[i] > highestDValue) {
                highestDValue = endGreen[i];
            }
            MyString labelString = new MyString(label[i]);
            if (dPoints.containsKey(labelString)) {
                double[] zwerg = dPoints.get(labelString);
                double[] tmp = new double[]{startBlue[i], endBlue[i],
                    endGreen[i]};
                double[] toInclude = new double[zwerg.length + 3];
                if ((tmp[0] + epsilon) >= zwerg[zwerg.length - 1]) {
                    if (tmp[0] < zwerg[zwerg.length - 1]) {
                        tmp[0] = zwerg[zwerg.length - 1];
                    }
                    int j;
                    for (j = 0; j < zwerg.length; j++) {
                        toInclude[j] = zwerg[j];
                    }
                    for (; j < toInclude.length; j++) {
                        toInclude[j] = tmp[j - zwerg.length];
                    }
                    dPoints.put(labelString, toInclude);
                } else if ((tmp[2] - epsilon) <= zwerg[0]) {
                    if (tmp[2] > zwerg[0]) {
                        tmp[2] = zwerg[0];
                        if (tmp[2] < tmp[1]) {
                            tmp[1] = tmp[2];
                        }
                    }
                    int j;
                    for (j = 0; j < 3; j++) {
                        toInclude[j] = tmp[j];
                    }
                    for (; j < toInclude.length; j++) {
                        toInclude[j] = zwerg[j - 3];
                    }
                    dPoints.put(labelString, toInclude);
                } else {
                    boolean includedBetween = false;
                    for (int j = 2; j < zwerg.length - 1; j += 3) {
                        if ((tmp[0] + epsilon) >= zwerg[j]
                                && (tmp[2] - epsilon) <= zwerg[j + 1]
                                && !includedBetween) {
                            if (tmp[0] < zwerg[j] && !includedBetween) {
                                tmp[0] = zwerg[j];
                            }
                            if (tmp[2] > zwerg[j + 1] && !includedBetween) {
                                tmp[2] = zwerg[j + 1];
                                if (tmp[2] < tmp[1]) {
                                    tmp[1] = tmp[2];
                                }
                            }
                            int k;
                            for (k = 0; k <= j; k++) {
                                toInclude[k] = zwerg[k];
                            }
                            for (; k <= j + 3; k++) {
                                toInclude[k] = tmp[k - j - 1];
                            }
                            for (; k < toInclude.length; k++) {
                                toInclude[k] = zwerg[k - 3];
                            }
                            dPoints.put(labelString, toInclude);
                            includedBetween = true;
                            break;
                        }
                    }
                    if (!includedBetween) {
                        System.out
                                .println("Problem at Label "
                                        + label[i]
                                        + ". Epsilon is either too small or your data is corrupted");
                        dPoints.put(new MyString("Error_" + label[i] + "_"
                                + errNr++), tmp);
                        System.out.println((tmp[2] + epsilon) <= zwerg[0]);
                    }
                }
            } else {
                dPoints.put(labelString, new double[]{startBlue[i],
                    endBlue[i], endGreen[i]});
            }
        }
        if (debug) {
            printDoublePoints();
        }
        createDoubleGraph(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void printDoublePoints() {
        DecimalFormat f = new DecimalFormat("#000.00");
        for (Entry<MyString, double[]> entry : dPoints.entrySet()) {
            MyString key = entry.getKey();
            System.out.print(key.getValue() + "\t");
            int i = 0;
            for (double l : entry.getValue()) {
                if ((i++) % 3 != 2) {
                    System.out.print(f.format(l) + " | ");
                } else {
                    System.out.print(f.format(l) + "; \t");
                }
            }
            System.out.println();
        }
    }

    private void printData(String title, double[] startBlue, double[] endBlue,
            double[] endGreen, String[] label, double epsilon, Color[] color) {
        System.out.print("printBars(\"" + title + "\",new double[]{");
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        double[] cur = null;
        for (int i = 0; i <= 2; i++) {
            if (i == 0) {
                cur = startBlue;
            }
            if (i == 1) {
                cur = endBlue;
            }
            if (i == 2) {
                cur = endGreen;
            }
            for (double d : cur) {
                sb.append(prefix);
                prefix = ", ";
                sb.append(d);
            }
            sb.append("},");
            System.out.println(sb.toString());
            if (i < 2) {
                System.out.print("\tnew double[]{");
            }
            sb = new StringBuilder();
            prefix = "";
        }
        System.out.print("\tnew String[]{");
        for (String s : label) {
            sb.append(prefix);
            prefix = ", ";
            sb.append("\"");
            sb.append(s);
            sb.append("\"");
        }
        sb.append("},");
        System.out.println(sb.toString());
        System.out.print("\t" + epsilon);
        sb = new StringBuilder();
        prefix = ",\nnew Color[]{";
        for (Color c : color) {
            if (c != null) {
                sb.append(prefix);
                prefix = ", ";
                sb.append("new Color(");
                sb.append(c.getRGB());
                sb.append(")");
            }
        }
        System.out.println(sb.toString() + ");");
        System.out.println();
    }

    private void createDoubleGraph(String title) {
        DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
        int maxLeng = 0; // laengster einzelner Balken (meiste Eintraege)

        // List<String> labels = new ArrayList<String>();
        // labels.addAll(dPoints.keySet());
        // Collections.sort(labels);
        for (Entry<MyString, double[]> entry : dPoints.entrySet()) {
            MyString elem = entry.getKey();
            double[] list = entry.getValue();
            for (int i = 0; i < list.length; i += 3) {
                double gap = list[i];
                if (i > 0) {
                    gap -= list[i - 1]; // gap = list[i]-list[i-1];
                }
                defaultcategorydataset.addValue(gap, i + "", elem.getValue()); // transparent
                defaultcategorydataset.addValue(list[i + 1] - list[i], (i + 1)
                        + "", elem.getValue()); // blue
                defaultcategorydataset.addValue(list[i + 2] - list[i + 1],
                        (i + 2) + "", elem.getValue()); // green
                // System.out.println(elem+" added "+gap+", "+(list[i+1] -
                // list[i])+", "+(list[i+2] - list[i+1]));
            }
            if (highestDValue > 0) {
                defaultcategorydataset.addValue(highestDValue
                        - list[list.length - 1], list.length + "",
                        elem.getValue());
                // end the bar with a transparent bar
            }
            if (list.length > maxLeng) {
                maxLeng = list.length;
            }
        }

        String newTitle = title;
        if (noTitle) {
            newTitle = "";
        }

        JFreeChart jfreechart = ChartFactory.createStackedBarChart(newTitle,
                labelLegendTitle, dataLegendTitle, defaultcategorydataset,
                PlotOrientation.HORIZONTAL, false, true, false);

        // jfreechart.setTitle(title);
        this.setTitle(title);

        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        numberaxis.setUpperMargin(0.0);
        numberaxis.setNumberFormatOverride(NumberFormat
                .getInstance(Locale.ENGLISH));

        jfreechart.getTitle().setFont(new Font(fontStyle, Font.BOLD, 40));
        numberaxis.setLabelFont(new Font(fontStyle, Font.BOLD, 30)); // "Time in sec"
        numberaxis.setTickLabelFont(new Font(fontStyle, Font.PLAIN, 20)); // "20 40 60 ..."
        categoryplot.getDomainAxis().setLabelFont(
                new Font(fontStyle, Font.BOLD, 30)); // "Module"
        categoryplot.getDomainAxis().setTickLabelFont(
                new Font(fontStyle, Font.PLAIN, 20)); // module-names

        BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
        barrenderer.setDrawBarOutline(false);
        Paint greenGradient = new GradientPaint(0.0F, 0.0F, Color.green, 0.0F,
                0.0F, new Color(0, 64, 0));
        Paint transparent = new Color(0, 0, 0, 0);
        Paint blueGradient = new GradientPaint(0.0F, 0.0F, Color.blue, 0.0F,
                0.0F, new Color(64, 0, 0));
        // Paint redGradient = new GradientPaint(0.0F, 0.0F, Color.red, 0.0F,
        // 0.0F, new Color(64, 0, 0));
        for (int i = 0; i < maxLeng; i += 3) {
            barrenderer.setSeriesPaint(i, transparent);
            if (colors[i / 3] != null) {
                barrenderer.setSeriesPaint(i + 1, new GradientPaint(0.0F, 0.0F,
                        colors[i / 3], 0.0F, 0.0F, new Color(64, 0, 0)));
            } else {
                barrenderer.setSeriesPaint(i + 1, blueGradient);
            }
            barrenderer.setSeriesPaint(i + 2, greenGradient);
        }
        barrenderer.setSeriesPaint(maxLeng, transparent);
        // barrenderer.setSeriesPaint(4, redGradient);

        JPanel jpanel = new ChartPanel(jfreechart);
        jpanel.setPreferredSize(new Dimension(1000, 600));
        setContentPane(jpanel);
    }
}
