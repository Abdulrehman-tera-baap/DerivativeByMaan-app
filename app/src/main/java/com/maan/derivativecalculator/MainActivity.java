package com.maan.derivativecalculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {
    static { System.loadLibrary("derivative-native"); }
    private native double polynomialDerivative(double[] coefficients, double x);
    private native double standardDerivative(int choice, double x);

    private final int ink = Color.rgb(27, 35, 55);
    private final int purple = Color.rgb(91, 76, 210);
    private LinearLayout root, content;
    private EditText degreeInput, coeffInput, xInput, standardX;
    private Spinner functionSpinner;
    private TextView polyResult, funcResult, polyFormula, standardFormula;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        getWindow().setStatusBarColor(Color.rgb(17,24,39));
        getWindow().setNavigationBarColor(Color.rgb(17,24,39));
        buildUi();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private EditText field(String hint) {
        EditText e = new EditText(this); e.setSingleLine(true); e.setTextSize(16);
        e.setHint(hint); e.setPadding(16, 10, 16, 10); e.setTextColor(ink);
        e.setHintTextColor(Color.GRAY); e.setBackground(shape(Color.WHITE, 18));
        return e;
    }
    private void addGap(int h) { View v = new View(this); content.addView(v, new LinearLayout.LayoutParams(1,h)); }
    private void label(String s) { content.addView(text(s,14,ink,true)); addGap(6); }
    private Button button(String s) {
        Button b = new Button(this); b.setText(s); b.setTextColor(Color.WHITE);
        b.setAllCaps(false); b.setTextSize(15); b.setBackground(shape(purple,18)); return b;
    }
    private void buildUi() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(244,246,251));
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20,22,20,28);
        LinearLayout header = new LinearLayout(this); header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(22,24,22,24); header.setBackground(shape(Color.rgb(17,24,39),24));
        header.addView(text("∂  DERIVATIVE CALCULATOR",12,0xFFB9B4FF,true));
        TextView title = text("Derivative Calculator",25,Color.WHITE,true); header.addView(title);
        TextView subtitle = text("by Maan  •  Mathematics made simpler",13,0xFFD1D5DB,false);
        header.addView(subtitle); content.addView(header); addGap(18);

        TextView modeTitle = text("Choose calculator mode",17,ink,true); content.addView(modeTitle); addGap(8);
        LinearLayout tabs = new LinearLayout(this); tabs.setOrientation(LinearLayout.HORIZONTAL);
        Button polyTab = button("Polynomial"); Button funcTab = button("Functions");
        LinearLayout.LayoutParams half = new LinearLayout.LayoutParams(0,54,1); half.setMargins(0,0,6,0);
        tabs.addView(polyTab,half); LinearLayout.LayoutParams half2 = new LinearLayout.LayoutParams(0,54,1); half2.setMargins(6,0,0,0); tabs.addView(funcTab,half2);
        content.addView(tabs); addGap(14);

        LinearLayout polyCard = card(); LinearLayout funcCard = card();
        content.addView(polyCard); content.addView(funcCard);
        buildPolynomial(polyCard); buildFunctions(funcCard);
        funcCard.setVisibility(View.GONE);
        polyTab.setOnClickListener(v->{polyCard.setVisibility(View.VISIBLE);funcCard.setVisibility(View.GONE);});
        funcTab.setOnClickListener(v->{polyCard.setVisibility(View.GONE);funcCard.setVisibility(View.VISIBLE);});
        TextView foot = text("Supports polynomial, trigonometric, exponential and logarithmic derivatives.",12,Color.GRAY,false);
        foot.setPadding(4,18,4,4); content.addView(foot);
        scroll.addView(content); root.addView(scroll); setContentView(root);
    }
    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(18,18,18,18); c.setBackground(shape(Color.WHITE,22));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,0,0,12); c.setLayoutParams(p); return c;
    }
    private void buildPolynomial(LinearLayout card) {
        card.addView(text("Polynomial derivative",19,ink,true)); addGap(6);
        card.addView(text("Enter coefficients from x⁰ to xⁿ, separated by commas.",13,Color.DKGRAY,false)); addGap(14);
        label("Degree (0–50)"); degreeInput=field("Example: 3"); card.addView(degreeInput); addGap(12);
        label("Coefficients (constant first)"); coeffInput=field("Example: 2, 3, 0, 4"); card.addView(coeffInput); addGap(12);
        label("Evaluate derivative at x"); xInput=field("Example: 2"); card.addView(xInput); addGap(14);
        Button calculate=button("Calculate derivative"); card.addView(calculate);
        polyFormula=text("Example: 4x³ + 3x + 2  →  12x² + 3",14,purple,true);
        polyFormula.setPadding(2,12,2,4); card.addView(polyFormula);
        calculate.setOnClickListener(v->calculatePolynomial());
    }
    private void buildFunctions(LinearLayout card) {
        card.addView(text("Standard functions",19,ink,true)); addGap(8);
        label("Select function");
        functionSpinner=new Spinner(this);
        String[] names={"sin(x)","cos(x)","tan(x)","eˣ","ln(x)","sqrt(x)"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,names);
        functionSpinner.setAdapter(adapter); card.addView(functionSpinner); addGap(12);
        label("Value of x (radians for sin/cos/tan)"); standardX=field("Enter x"); card.addView(standardX); addGap(14);
        Button calculate=button("Calculate derivative"); card.addView(calculate);
        standardFormula=text("The derivative rule will appear here.",14,purple,true);
        standardFormula.setPadding(2,12,2,4); card.addView(standardFormula);
        funcResult=resultView(); card.addView(funcResult);
        calculate.setOnClickListener(v->calculateFunction());
    }
    private double read(EditText e) { return Double.parseDouble(e.getText().toString().trim()); }
    private TextView resultView() {
        TextView t=text("",16,Color.rgb(20,65,48),true);
        t.setPadding(16,18,16,18); t.setBackground(shape(Color.rgb(224,247,236),18));
        t.setVisibility(View.GONE); return t;
    }
    private void showResult(String s, TextView target) {
        target.setText(s); target.setVisibility(View.VISIBLE);
    }

    private void calculatePolynomial() {
        try {
            int degree=Integer.parseInt(degreeInput.getText().toString().trim());
            if(degree<0||degree>50) throw new IllegalArgumentException("Degree must be between 0 and 50.");
            String[] parts=coeffInput.getText().toString().trim().split(",");
            if(parts.length!=degree+1) throw new IllegalArgumentException("Please enter exactly "+(degree+1)+" coefficients.");
            double[] c=new double[parts.length];
            for(int i=0;i<parts.length;i++) c[i]=Double.parseDouble(parts[i].trim());
            double x=read(xInput), value=polynomialDerivative(c,x);
            StringBuilder f=new StringBuilder(), d=new StringBuilder();
            for(int i=degree;i>=0;i--) if(c[i]!=0) appendTerm(f,c[i],i);
            for(int i=degree;i>=1;i--) if(c[i]*i!=0) appendTerm(d,c[i]*i,i-1);
            if(f.length()==0) f.append("0"); if(d.length()==0) d.append("0");
            polyFormula.setText("f(x) = "+f+"\nf′(x) = "+d);
            showResult("f′("+fmt(x)+") = "+fmt(value), polyResult);
        } catch(Exception ex) { error(ex.getMessage()==null?"Check your inputs.":ex.getMessage()); }
    }
    private void appendTerm(StringBuilder b,double a,int power) {
        if(b.length()>0 && a>0) b.append(" + "); else if(a<0) b.append(" - ");
        double abs=Math.abs(a);
        if(power==0 || abs!=1) b.append(fmt(abs));
        if(power>0) { if(abs!=1)b.append(" "); b.append("x"); if(power>1)b.append("⁽"+power+"⁾"); }
    }
    private void calculateFunction() {
        try {
            int choice=functionSpinner.getSelectedItemPosition()+1; double x=read(standardX);
            if(choice==5 && x<=0) throw new IllegalArgumentException("For ln(x), x must be greater than 0.");
            if(choice==6 && x<=0) throw new IllegalArgumentException("For sqrt(x) derivative, x must be greater than 0.");
            if(choice==3 && Math.abs(Math.cos(x))<1e-12) throw new IllegalArgumentException("tan(x) derivative is undefined at this x.");
            double value=standardDerivative(choice,x);
            String[] rules={"d/dx sin(x) = cos(x)","d/dx cos(x) = −sin(x)","d/dx tan(x) = sec²(x)","d/dx eˣ = eˣ","d/dx ln(x) = 1/x","d/dx sqrt(x) = 1/(2√x)"};
            standardFormula.setText(rules[choice-1]);
            showResult("Derivative at x = "+fmt(x)+"\nResult = "+String.format(Locale.US,"%.8f",value));
        } catch(Exception ex) { error(ex.getMessage()==null?"Check your inputs.":ex.getMessage()); }
    }
    private String fmt(double d) { return String.format(Locale.US,"%g",d); }
    private void error(String s) {
        new android.app.AlertDialog.Builder(this).setTitle("Input error").setMessage(s).setPositiveButton("OK",null).show();
    }
}
