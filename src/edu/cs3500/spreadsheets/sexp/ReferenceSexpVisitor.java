package edu.cs3500.spreadsheets.sexp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.Reference;

public class ReferenceSexpVisitor implements SexpVisitor<ArrayList<Coord>> {
  private ArrayList<Coord> refList;

  public ReferenceSexpVisitor() {
    this.refList = new ArrayList<>();
  }

  @Override
  public ArrayList<Coord> visitBoolean(boolean b) {
    return this.refList;
  }

  @Override
  public ArrayList<Coord> visitNumber(double d) {
    return this.refList;
  }

  @Override
  public ArrayList<Coord> visitSList(List<Sexp> l) {
    FunctionCheckSexpVisitor functionChecker = new FunctionCheckSexpVisitor();
    if (l.get(0).accept(functionChecker)) {
      ArrayList<Coord> innerRefs = new ArrayList<>();
      ReferenceSexpVisitor innerRefVisitor = new ReferenceSexpVisitor();
      for (int i = 1; i < l.size(); i++) {
        if (i != l.size() - 1) {
          l.get(i).accept(innerRefVisitor);
        } else {
          innerRefs.addAll(l.get(i).accept(innerRefVisitor));
        }
      }

      this.refList.addAll(innerRefs);
    }

    return this.refList;
  }

  @Override
  public ArrayList<Coord> visitString(String s) {
    return this.refList;
  }

  @Override
  public ArrayList<Coord> visitSymbol(String s) {
    if (validReference(s)) {
      this.refList.add(parseCoord(s));
    } else if(s.contains(":")) {
      int colonIndex = s.indexOf(":");
      String firstRef = s.substring(0, colonIndex);
      String secondRef = s.substring(colonIndex + 1);
      if (validReference(firstRef) && validReference(secondRef)) {
        Coord firstCoord = parseCoord(firstRef);
        Coord secondCoord = parseCoord(secondRef);
        for (int i = firstCoord.col; i <= secondCoord.col; i++) {
          for (int j = firstCoord.row; j <= secondCoord.row; i++) {
            this.refList.add(new Coord(i, j));
          }
        }
      }
    }

    return this.refList;
  }

  private boolean validReference(String s) {
    Pattern p = Pattern.compile("[^a-zA-Z0-9]");
    boolean validChars = !p.matcher(s).find();

    if (!validChars) {
      return false;
    }

    int firstNum = 0;
    int lastLetter = 1;

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (Character.isDigit(c)) {
        firstNum = i;
        break;
      }
    }

    for (int j = s.length() - 1; j > 0; j--) {
      char d = s.charAt(j);
      if (Character.isLetter(d)) {
        lastLetter = j;
        break;
      }
    }

    return firstNum > lastLetter;
  }

  private Coord parseCoord(String s) {
    int firstNum = 0;
    int lastLetter = 0;

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (Character.isDigit(c)) {
        firstNum = i;
        break;
      }
    }

    for (int j = s.length() - 1; j > 0; j--) {
      char d = s.charAt(j);
      if (Character.isLetter(d)) {
        lastLetter = j;
        break;
      }
    }

    int col = Coord.colNameToIndex(s.substring(0, lastLetter + 1));
    int row = Integer.parseInt(s.substring(firstNum));

    return new Coord(col, row);
  }
}
