import java.io.*;
import java.nio.charset.Charset;
import java.util.*;



public class Main
{
    static String[] inputFiles;
    static SrcFile[] inputSrcs;

    static double threshold = 0.3;

    static boolean[][] matrix;


    public static void main(String[] args) throws IOException, InterruptedException
    {
        fillInputFiles();
        fillInputSrcs();
        fillGroups();

    }

    private static void fillInputFiles() throws IOException
    {
        List<String> inputLines = readFileLines("input.txt");
        int count = Integer.parseInt(inputLines.get(0).trim());

        inputFiles = new String[count];

        for (int i = 0; i < count; i++)
        {
            inputFiles[i] = inputLines.get(i + 1);
        }
    }

    private static List<String> readFileLines(String path) throws IOException
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

    private static void printRes(List<String> strings) throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
        writer.println(strings.size());
        for (int i = 0; i < strings.size(); i++)
        {
            writer.println(strings.get(i));
        }
        writer.close();
    }

    private static void fillInputSrcs() throws IOException
    {
        inputSrcs = new SrcFile[inputFiles.length];
        for (int i = 0; i < inputFiles.length; i++)
        {
            inputSrcs[i] = new SrcFile(inputFiles[i].trim());
        }
    }


    private static void fillGroups() throws FileNotFoundException, UnsupportedEncodingException
    {
        matrix = new boolean[inputSrcs.length][inputSrcs.length];

        for (int i = 0; i < inputSrcs.length; i++)
        {
            for (int j = 0; j < inputSrcs.length; j++)
            {
                if (i < j)
                {
                    double dist = editdist(inputSrcs[i].getString(), inputSrcs[j].getString());
                    // System.out.println(String.format("[ %s ]  ----  [ %s ]  Result: %s", inputSrcs[i].path, inputSrcs[j].path, dist));
                    if (dist <= threshold)
                    {
                        matrix[i][j] = matrix[j][i] = true;
                    }
                }
            }
        }
        //printMatrix();
        vNum = inputSrcs.length;
        solve();

        List<String> strings = new ArrayList<String>();
        for (ArrayList<SrcFile> curr : groups.values())
        {
            if (curr.size() > 1)
            {
                String groupStr = "";
                for (SrcFile cheater : curr)
                {
                    groupStr += cheater.path + " ";
                }
                strings.add(groupStr.trim());
            }
        }

        printRes(strings);
    }


    static Map<Integer, ArrayList<SrcFile>> groups = new HashMap<Integer, ArrayList<SrcFile>>();

    static int vNum; // количество вершин
    static boolean[] used; // массив пометок
    static int[] cc;
    static int ccNum;

    static void solve()
    {
        used = new boolean[vNum]; // массив пометок
        cc = new int[vNum]; // сс[v] = номер компоненты, к которой принадлежит v
        ccNum = 0; // количество компонент

        for (int v = 0; v < vNum; v++)
        { // перебираем вершины
            if (!used[v])
            { // если текущая не помечена
                ccNum++; // значит мы нашли компоненту связности
                dfs(v); // запускаем на ней DFS
            }
        }

        for (int i = 0; i < cc.length; i++)
        {
            if (groups.containsKey(cc[i]))
            {
                groups.get(cc[i]).add(inputSrcs[i]);
            } else
            {
                ArrayList<SrcFile> currArr = new ArrayList<SrcFile>();
                groups.put(cc[i], currArr);
                currArr.add(inputSrcs[i]);
            }
        }
    }

    static void dfs(int v)
    {
        used[v] = true;
        cc[v] = ccNum; // ставим текущей вершине в соответствие номер компоненты
        for (int nv = 0; nv < vNum; nv++)
            if (!used[nv] && matrix[v][nv])
                dfs(nv);
    }


    private static void printMatrix()
    {
        for (int i = 0; i < inputSrcs.length; i++)
        {
            for (int j = 0; j < inputSrcs.length; j++)
            {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }


    private static double editdist(String S1, String S2)
    {
        int maxLength = Math.max(S1.length(), S2.length());

        int m = S1.length(), n = S2.length();
        int[] D1;
        int[] D2 = new int[n + 1];

        for (int i = 0; i <= n; i++)
            D2[i] = i;

        for (int i = 1; i <= m; i++)
        {
            D1 = D2;
            D2 = new int[n + 1];
            for (int j = 0; j <= n; j++)
            {
                if (j == 0) D2[j] = i;
                else
                {
                    int cost = (S1.charAt(i - 1) != S2.charAt(j - 1)) ? 1 : 0;
                    if (D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost)
                        D2[j] = D2[j - 1] + 1;
                    else if (D1[j] < D1[j - 1] + cost)
                        D2[j] = D1[j] + 1;
                    else
                        D2[j] = D1[j - 1] + cost;
                }
            }
        }
        return (double) D2[n] / (double) maxLength;
    }

}


