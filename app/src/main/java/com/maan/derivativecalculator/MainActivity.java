package com.maan.derivativecalculator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.math.BigDecimal;
import java.math.MathContext;

public class MainActivity extends Activity {
    static { System.loadLibrary("derivative-native"); }
    private native double polynomialDerivative(double[] coefficients, double x);
    private native double standardDerivative(int choice, double x);

    private static final int DARK = Color.rgb(30, 27, 75);
    private static final int PURPLE = Color.rgb(91, 76, 210);
    private static final int INK = Color.rgb(27, 35, 55);
    private static final int MUTED = Color.rgb(107, 114, 128);
    private static final int BG = Color.rgb(243, 244, 251);
    private static final int BORDER = Color.rgb(209, 213, 232);

    private float density;
    private View statusSpace;
    private LinearLayout content, polyCard, funcCard;
    private TextView polyTab, funcTab, polyFormula, polyResult, funcFormula, funcResult;
    private EditText degreeInput, coeffInput, xInput, funcX;
    private Spinner functionSpinner;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        density = getResources().getDisplayMetrics().density;
        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(BG);
        buildUi();
    }

    // ---------- small helpers ----------
    private int dp(int v) { return Math.round(v * density); }

    private GradientDrawable shape(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        return d;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private void add(LinearLayout parent, View v, int topDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(topDp);
        parent.addView(v, lp);
    }

    private void labeled(LinearLayout card, String label, View input) {
        add(card, text(label, 13, MUTED, true), 16);
        add(card, input, 6);
    }

    private GradientDrawable fieldShape() {
        GradientDrawable g = shape(Color.rgb(249, 250, 253), 14);
        g.setStroke(dp(1), BORDER);
        return g;
    }

    private EditText field(String hint, int inputType) {
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setInputType(inputType);
        e.setHint(hint);
        e.setTextSize(16);
        e.setTextColor(INK);
        e.setHintTextColor(Color.rgb(156, 163, 175));
        e.setBackground(fieldShape());
        e.setPadding(dp(14), dp(12), dp(14), dp(12));
        return e;
    }

    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setTextSize(16);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(shape(PURPLE, 16));
        b.setStateListAnimator(null);
        b.setMinHeight(dp(52));
        b.setMinimumHeight(dp(52));
        return b;
    }

    private TextView infoBox(int bgColor, int fgColor, int sp) {
        TextView t = text("", sp, fgColor, true);
        t.setBackground(shape(bgColor, 14));
        t.setPadding(dp(16), dp(14), dp(16), dp(14));
        t.setVisibility(View.GONE);
        return t;
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setBackground(shape(Color.WHITE, 22));
        c.setPadding(dp(20), dp(20), dp(20), dp(22));
        c.setElevation(dp(3));
        return c;
    }

    private TextView tab(String s) {
        TextView t = text(s, 15, MUTED, true);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(8), dp(12), dp(8), dp(12));
        return t;
    }

    private void selectTab(boolean poly) {
        polyTab.setBackground(shape(poly ? PURPLE : Color.TRANSPARENT, 14));
        polyTab.setTextColor(poly ? Color.WHITE : MUTED);
        funcTab.setBackground(shape(poly ? Color.TRANSPARENT : PURPLE, 14));
        funcTab.setTextColor(poly ? MUTED : Color.WHITE);
        polyCard.setVisibility(poly ? View.VISIBLE : View.GONE);
        funcCard.setVisibility(poly ? View.GONE : View.VISIBLE);
    }

    // ---------- screen ----------
    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // space that sits under the status bar (height set from window insets)
        statusSpace = new View(this);
        statusSpace.setBackgroundColor(DARK);
        root.addView(statusSpace, new LinearLayout.LayoutParams(-1, 0));

        // header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable hg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{DARK, Color.rgb(79, 70, 229)});
        float r = dp(28);
        hg.setCornerRadii(new float[]{0, 0, 0, 0, r, r, r, r});
        header.setBackground(hg);
        header.setPadding(dp(22), dp(10), dp(22), dp(26));
        TextView badge = text("\u2202  DERIVATIVE CALCULATOR", 11, 0xFFC4BFFF, true);
        badge.setLetterSpacing(0.12f);
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        topRow.addView(badge, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView helpBtn = text("?  Help", 13, Color.WHITE, true);
        helpBtn.setBackground(shape(0x33FFFFFF, 16));
        helpBtn.setPadding(dp(14), dp(7), dp(14), dp(7));
        helpBtn.setOnClickListener(v -> showHelp());
        topRow.addView(helpBtn);
        header.addView(topRow);
        add(header, text("Derivative Calculator", 27, Color.WHITE, true), 6);
        add(header, text("by Maan  \u2022  Mathematics made simpler", 13, 0xFFD1D5DB, false), 4);
        root.addView(header, new LinearLayout.LayoutParams(-1, -2));

        // scrolling content
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        setContentPadding(0);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        add(content, text("Choose calculator mode", 14, MUTED, true), 0);
        polyTab = tab("Polynomial");
        funcTab = tab("Functions");
        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setBackground(shape(Color.WHITE, 18));
        tabs.setPadding(dp(5), dp(5), dp(5), dp(5));
        tabs.setElevation(dp(2));
        tabs.addView(polyTab, new LinearLayout.LayoutParams(0, -2, 1f));
        tabs.addView(funcTab, new LinearLayout.LayoutParams(0, -2, 1f));
        add(content, tabs, 8);

        polyCard = buildPolynomialCard();
        funcCard = buildFunctionCard();
        add(content, polyCard, 16);
        add(content, funcCard, 16);

        TextView foot = text("Supports polynomial, trigonometric, exponential and logarithmic derivatives.", 12, MUTED, false);
        foot.setGravity(Gravity.CENTER);
        add(content, foot, 20);

        polyTab.setOnClickListener(v -> selectTab(true));
        funcTab.setOnClickListener(v -> selectTab(false));
        selectTab(true);

        root.setOnApplyWindowInsetsListener((v, insets) -> {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) statusSpace.getLayoutParams();
            lp.height = insets.getSystemWindowInsetTop();
            statusSpace.setLayoutParams(lp);
            setContentPadding(insets.getSystemWindowInsetBottom());
            return insets;
        });
        setContentView(root);
    }

    private void setContentPadding(int bottomInsetPx) {
        content.setPadding(dp(16), dp(18), dp(16), dp(28) + bottomInsetPx);
    }

    private LinearLayout buildPolynomialCard() {
        LinearLayout c = card();
        c.addView(text("Polynomial derivative", 19, INK, true));
        add(c, text("Enter the coefficients starting from the constant term, separated by commas.", 13, MUTED, false), 6);

        degreeInput = field("Example: 3", InputType.TYPE_CLASS_NUMBER);
        coeffInput = field("Example: 2, 3, 0, 4", InputType.TYPE_CLASS_TEXT);
        xInput = field("Example: 2", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        labeled(c, "Degree of polynomial (optional, 0\u201350)", degreeInput);
        labeled(c, "Coefficients (constant term first)", coeffInput);
        labeled(c, "Find derivative at x =", xInput);

        Button calc = button("Calculate derivative");
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2);
        bp.topMargin = dp(22);
        c.addView(calc, bp);
        calc.setOnClickListener(v -> calculatePolynomial());

        TextView example = text("Try an example", 14, PURPLE, true);
        example.setGravity(Gravity.CENTER);
        example.setPadding(dp(8), dp(12), dp(8), dp(4));
        example.setOnClickListener(v -> {
            degreeInput.setText("3");
            coeffInput.setText("2, 3, 0, 4");
            xInput.setText("2");
        });
        add(c, example, 4);

        polyFormula = infoBox(Color.rgb(238, 236, 255), PURPLE, 15);
        polyResult = infoBox(Color.rgb(224, 247, 236), Color.rgb(20, 83, 45), 19);
        add(c, polyFormula, 12);
        add(c, polyResult, 10);
        return c;
    }

    private LinearLayout buildFunctionCard() {
        LinearLayout c = card();
        c.addView(text("Standard functions", 19, INK, true));
        add(c, text("Pick a function and the value of x to get its derivative.", 13, MUTED, false), 6);

        functionSpinner = new Spinner(this);
        String[] names = {"sin(x)", "cos(x)", "tan(x)", "e^x", "ln(x)", "sqrt(x)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        functionSpinner.setAdapter(adapter);
        LinearLayout spBox = new LinearLayout(this);
        spBox.setBackground(fieldShape());
        spBox.setPadding(dp(8), dp(2), dp(8), dp(2));
        spBox.addView(functionSpinner, new LinearLayout.LayoutParams(-1, dp(48)));
        labeled(c, "Select function", spBox);

        funcX = field("Enter x", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        labeled(c, "Value of x (in radians for sin, cos, tan)", funcX);

        Button calc = button("Calculate derivative");
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2);
        bp.topMargin = dp(22);
        c.addView(calc, bp);
        calc.setOnClickListener(v -> calculateFunction());

        funcFormula = infoBox(Color.rgb(238, 236, 255), PURPLE, 15);
        funcResult = infoBox(Color.rgb(224, 247, 236), Color.rgb(20, 83, 45), 19);
        add(c, funcFormula, 18);
        add(c, funcResult, 10);
        return c;
    }

    // ---------- calculations ----------
    private double readNumber(EditText e, String what) {
        String s = e.getText().toString().trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Please enter " + what + ".");
        return Double.parseDouble(s);
    }

    private void calculatePolynomial() {
        hideKeyboard();
        try {
            String raw = coeffInput.getText().toString().trim();
            if (raw.isEmpty()) throw new IllegalArgumentException("Please enter the coefficients.");
            String[] parts = raw.split("[,\\s]+");
            String d = degreeInput.getText().toString().trim();
            int degree = d.isEmpty() ? parts.length - 1 : Integer.parseInt(d);
            if (degree < 0 || degree > 50) throw new IllegalArgumentException("Degree must be between 0 and 50.");
            if (parts.length != degree + 1)
                throw new IllegalArgumentException("For degree " + degree + " please enter exactly " + (degree + 1) + " coefficients (constant term first).");
            double[] c = new double[parts.length];
            for (int i = 0; i < parts.length; i++) c[i] = Double.parseDouble(parts[i]);
            double x = readNumber(xInput, "the value of x");
            double value = polynomialDerivative(c, x);

            StringBuilder f = new StringBuilder(), dv = new StringBuilder();
            for (int i = degree; i >= 0; i--) if (c[i] != 0) appendTerm(f, c[i], i);
            for (int i = degree; i >= 1; i--) if (c[i] * i != 0) appendTerm(dv, c[i] * i, i - 1);
            if (f.length() == 0) f.append("0");
            if (dv.length() == 0) dv.append("0");

            show(polyFormula, "f(x) = " + f + "\nf\u2032(x) = " + dv);
            show(polyResult, "f\u2032(" + fmt(x) + ") = " + fmt(value));
        } catch (NumberFormatException ex) {
            error("Please enter valid numbers only.");
        } catch (Exception ex) {
            error(ex.getMessage() == null ? "Please check your inputs." : ex.getMessage());
        }
    }

    private void calculateFunction() {
        hideKeyboard();
        try {
            int choice = functionSpinner.getSelectedItemPosition() + 1;
            double x = readNumber(funcX, "the value of x");
            if ((choice == 5 || choice == 6) && x <= 0)
                throw new IllegalArgumentException(choice == 5 ? "For ln(x), x must be greater than 0." : "For sqrt(x), x must be greater than 0.");
            if (choice == 3 && Math.abs(Math.cos(x)) < 1e-12)
                throw new IllegalArgumentException("The derivative of tan(x) is undefined at this x.");
            double value = standardDerivative(choice, x);
            String[] rules = {
                "d/dx sin(x) = cos(x)", "d/dx cos(x) = \u2212sin(x)", "d/dx tan(x) = sec\u00B2(x)",
                "d/dx e\u02E3 = e\u02E3", "d/dx ln(x) = 1/x", "d/dx \u221Ax = 1 / (2\u221Ax)"};
            show(funcFormula, rules[choice - 1]);
            show(funcResult, "Derivative at x = " + fmt(x) + "\nResult = " + fmt(value));
        } catch (NumberFormatException ex) {
            error("Please enter a valid number for x.");
        } catch (Exception ex) {
            error(ex.getMessage() == null ? "Please check your inputs." : ex.getMessage());
        }
    }


    // ---------- help ----------
    private void helpSection(LinearLayout box, String title, String body) {
        add(box, text(title, 16, PURPLE, true), 16);
        TextView b = text(body, 14, INK, false);
        b.setLineSpacing(0, 1.15f);
        add(box, b, 4);
    }

    private void showHelp() {
        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(22), dp(4), dp(22), dp(8));
        sv.addView(box);

        helpSection(box, "1. Polynomial derivative",
            "Pick the Polynomial tab. Write the coefficients starting from the CONSTANT term first, then x, then x^2, and so on. Separate them with commas.\n\n"
            + "Example: 4x^3 + 3x + 2\n"
            + "constant = 2, x = 3, x^2 = 0, x^3 = 4\n"
            + "So type:  2, 3, 0, 4\n\n"
            + "(Urdu: sab se pehle constant likhein, phir x, phir x^2... Comma laga kar alag karein.)");

        helpSection(box, "2. Missing powers must be 0",
            "If a power is missing, write 0 for it. For 5x^3 + 2 type:  2, 0, 0, 5\n"
            + "Negative numbers (-4) and decimals (2.5) are allowed.\n\n"
            + "(Urdu: jo power na ho uski jagah 0 likhein. Sirf numbers likhein, x ya ^ na likhein.)");

        helpSection(box, "3. Degree and x",
            "Degree is the highest power (3 in the example). It is optional: leave it empty and the app finds it for you.\n"
            + "\"Find derivative at x\" is the number where you want the answer. For x = 2 the example gives f\u2032(2) = 51 and f\u2032(x) = 12x\u00B2 + 3.\n\n"
            + "(Urdu: Degree sab se bari power hai, khali chhor sakte hain. x mein wo number likhein jis par jawab chahiye.)");

        helpSection(box, "4. Standard functions",
            "Pick the Functions tab, choose sin, cos, tan, e^x, ln or sqrt, enter x and tap Calculate derivative.\n\n"
            + "\u2022 For sin, cos and tan, x must be in RADIANS. Degrees \u00D7 0.01745 = radians (45\u00B0 = 0.7854, 90\u00B0 = 1.5708).\n"
            + "\u2022 ln(x) and sqrt(x) need x greater than 0.\n"
            + "\u2022 tan(x) has no derivative at 90\u00B0 (1.5708).\n\n"
            + "(Urdu: sin, cos, tan mein x radians mein dein. ln aur sqrt ke liye x 0 se bara ho.)");

        helpSection(box, "5. Quick tips",
            "\u2022 Tap \"Try an example\" to fill the boxes automatically.\n"
            + "\u2022 Use a dot for decimals (2.5), not a comma.\n"
            + "\u2022 The result appears below the Calculate button, so scroll down if you cannot see it.");

        new android.app.AlertDialog.Builder(this)
            .setTitle("How to use")
            .setView(sv)
            .setPositiveButton("Got it", null)
            .show();
    }

    // ---------- formatting ----------
    private void show(TextView t, String s) { t.setText(s); t.setVisibility(View.VISIBLE); }

    private String sup(int n) {
        String map = "\u2070\u00B9\u00B2\u00B3\u2074\u2075\u2076\u2077\u2078\u2079";
        StringBuilder b = new StringBuilder();
        for (char ch : String.valueOf(n).toCharArray()) b.append(map.charAt(ch - '0'));
        return b.toString();
    }

    private void appendTerm(StringBuilder b, double a, int power) {
        boolean first = b.length() == 0;
        if (a < 0) b.append(first ? "\u2212" : " \u2212 ");
        else if (!first) b.append(" + ");
        double abs = Math.abs(a);
        if (power == 0 || abs != 1) b.append(fmt(abs));
        if (power > 0) { b.append("x"); if (power > 1) b.append(sup(power)); }
    }

    private String fmt(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return String.valueOf(v);
        if (v == Math.rint(v) && Math.abs(v) < 1e12) return String.valueOf((long) v);
        return new BigDecimal(v).round(new MathContext(8)).stripTrailingZeros().toPlainString();
    }

    private void hideKeyboard() {
        View f = getCurrentFocus();
        if (f != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
        }
    }

    private void error(String s) {
        new android.app.AlertDialog.Builder(this).setTitle("Input error").setMessage(s)
                .setPositiveButton("OK", null).show();
    }
}
