import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 24.04.2015.
 */
public class SrcFile
{
    String path;
    Lang language;
    List<String> fileLines;

    public SrcFile(String path) throws IOException
    {
        language = getType(path);
        fileLines = readFileLines(path);
        this.path = path;
        removeShit();
    }

    private Lang getType(String str)
    {
        String type = str.substring(str.lastIndexOf('.') + 1).trim().toLowerCase();
        if (type.equals("cpp"))
        {
            return Lang.CPP;
        } else if (type.equals("java"))
        {
            return Lang.CPP;
        } else if (type.equals("cs"))
        {
            return Lang.CPP;
        } else if (type.equals("c"))
        {
            return Lang.CPP;
        } else if (type.equals("pas"))
        {
            return Lang.PASCAL;
        } else if (type.equals("py"))
        {
            return Lang.PYTHON;
        } else
        {
            return Lang.UNKNOWN;
        }
    }

    private List<String> readFileLines(String path) throws IOException
    {
        InputStream fis;
        fis = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);
        List<String> readLines = new ArrayList<String>();
        String currLine;
        while ((currLine = br.readLine()) != null)
        {
            readLines.add(currLine);
        }
        return readLines;
    }

    //removes comments, includes, regions
    private void removeShit()
    {
        switch (language)
        {
            case PASCAL:
                removePascalTypeComments();
                removeWhiteSpaces();
                break;
            case CPP:
                removeCppTypeComments();
                removeWhiteSpaces();
                break;
            case PYTHON:
                removePythonTypeComments();
                break;
            case UNKNOWN:
                removeCppTypeComments();
                removeWhiteSpaces();
                break;
        }
        //in the end cuz' lines can be empty after removing comments
        removeEmptyLines();//we don't need them in any language

    }

    private void removeEmptyLines()
    {
        for (int i = fileLines.size() - 1; i >= 0; i--)
        {
            if (isWhitespaceOrEmpty(fileLines.get(i)))
            {
                fileLines.remove(i);
            }
        }
    }

    private void removeWhiteSpaces()
    {
        //not implemented yet
    }


    private static boolean isWhitespaceOrEmpty(String s)
    {
        int length = s.length();
        if (length > 0)
        {
            for (int i = 0; i < length; i++)
            {
                if (!Character.isWhitespace(s.charAt(i)))
                {
                    return false;
                }
            }
            return true;
        } else
            return true;
    }

    // removes /**/ and // comments
    private void removeCppTypeComments()
    {
        boolean commentStarted = false;
        for (int i = 0; i < fileLines.size(); i++)
        {
            String currStr = fileLines.get(i);

            if (!commentStarted)
            {
                //   // comments
                int indexComment = currStr.indexOf("//");
                if (indexComment != -1)
                {
                    currStr = currStr.substring(0, indexComment);
                    fileLines.set(i, currStr);
                    continue;
                }

                // /**/ comment
                int indexCommentStart = currStr.indexOf("/*");
                if (indexCommentStart != -1)
                {
                    commentStarted = true;
                    int indexCommentEnd = currStr.lastIndexOf("*/");
                    if (indexCommentEnd == -1)
                        currStr = currStr.substring(0, indexCommentStart);
                    else
                    {
                        currStr = currStr.substring(0, indexCommentStart) + currStr.substring(indexCommentEnd + 2);
                        commentStarted = false;
                    }

                    fileLines.set(i, currStr);
                }

            } else
            {
                int indexCommentEnd = currStr.indexOf("*/");

                if (indexCommentEnd != -1)
                {
                    commentStarted = false;
                    currStr = currStr.substring(indexCommentEnd + 2);
                    fileLines.set(i, currStr);
                } else
                {
                    //remove this line
                    fileLines.remove(i);
                    i--;
                }
            }
        }
    }

    private void removePascalTypeComments()
    {
        boolean commentStarted = false;
        for (int i = 0; i < fileLines.size(); i++)
        {
            String currStr = fileLines.get(i);

            if (!commentStarted)
            {
                // {} comment
                int indexCommentStart = currStr.indexOf("{");
                if (indexCommentStart != -1)
                {
                    commentStarted = true;
                    int indexCommentEnd = currStr.lastIndexOf("}");
                    if (indexCommentEnd == -1)
                        currStr = currStr.substring(0, indexCommentStart);
                    else
                    {
                        currStr = currStr.substring(0, indexCommentStart) + currStr.substring(indexCommentEnd + 1);
                        commentStarted = false;
                    }

                    fileLines.set(i, currStr);
                }
            } else
            {
                int indexCommentEnd = currStr.indexOf("}");

                if (indexCommentEnd != -1)
                {
                    commentStarted = false;
                    currStr = currStr.substring(indexCommentEnd + 1);
                    fileLines.set(i, currStr);
                } else
                {
                    //remove this line
                    fileLines.remove(i);
                    i--;
                }
            }

        }
    }

    // removes " " " and # comments
    private void removePythonTypeComments()
    {
        boolean commentStarted = false;
        for (int i = 0; i < fileLines.size(); i++)
        {
            String currStr = fileLines.get(i);

            if (!commentStarted)
            {
                //   // comments
                int indexComment = currStr.indexOf("#");
                if (indexComment != -1)
                {
                    currStr = currStr.substring(0, indexComment);
                    fileLines.set(i, currStr);
                    continue;
                }
                // /* comment
                int indexCommentStart = currStr.indexOf("\"\"\"");
                if (indexCommentStart != -1)
                {
                    commentStarted = true;

                    int indexCommentEnd = currStr.lastIndexOf("\"\"\"");
                    if (indexCommentEnd == indexCommentStart)
                        currStr = currStr.substring(0, indexCommentStart);
                    else
                    {
                        currStr = currStr.substring(0, indexCommentStart) + currStr.substring(indexCommentEnd + 3);
                        commentStarted = false;
                    }
                    fileLines.set(i, currStr);
                }
            } else
            {
                int indexCommentEnd = currStr.indexOf("\"\"\"");

                if (indexCommentEnd != -1)
                {
                    commentStarted = false;
                    currStr = currStr.substring(indexCommentEnd + 3);
                    fileLines.set(i, currStr);
                } else
                {
                    //remove this line
                    fileLines.remove(i);
                    i--;
                }
            }
        }
    }

    public String getString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fileLines.size(); i++)
        {
            stringBuilder.append(fileLines.get(i));
        }
        return stringBuilder.toString();
    }
}
