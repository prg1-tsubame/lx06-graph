# lx06-graph: グラフの表現と探索

グラフを隣接リストで表現し、読み込み・出力・探索・距離計算・トポロジカルソートを行うサンプルです。ソースは [src/graph.scala](src/graph.scala) にあります。

## データ形式

`data/*.csv` の第 *i* 行（0 始まり）が頂点 *i* の隣接頂点をカンマ区切りで表します。空行は隣接頂点なしを表します。

- `data/undirected.csv`: 無向グラフ
- `data/directed.csv`: 有向グラフ

## 実行方法

プロジェクトのディレクトリで sbt の `run` を実行します。`@main` 関数が複数あるため、実行するものを選ぶメニューが表示されます。

```
$ sbt run
Multiple main classes detected. Select one to run:
 [1] graph.ex1_io
 [2] graph.ex2_traverse
 [3] graph.ex3_distances
 [4] graph.ex4_topological_sort
```

実行したいものの番号を入力します。各番号と、次節で解説する `@main` 関数の対応は上のとおりです。ソースは `package graph` 内にあるため、クラス名は `graph.` で始まります。

メニューを経由せずに直接実行するには、`sbt "runMain graph.ex1_io"` のようにクラス名を指定します。

## `@main` 関数の解説

### `ex1_io`: グラフの読み込みと出力

メニュー項目 [1] `graph.ex1_io`

`data/undirected.csv` を `G.load` で読み込み、次の 2 通りの形式で表示します。

- 隣接リスト形式 (`G.output`): 「頂点: 隣接頂点, ...」の形式です。
- 隣接行列形式 (`G.output_matrix`): 辺がある組を `x` で表します。辺の有無は `G.is_connected` が二分探索で判定します。`load` が隣接頂点を昇順に整列しているので、二分探索が使えます。

出力の形式（抜粋）:

```
隣接リスト形式
0: 1,2,4          ← 頂点 0 の隣接頂点は 1, 2, 4
1: 0,3,8
...
マトリックス形式
0   x x   x        ← 行 = 頂点 0。列 j = 0..8 のうち 1, 2, 4 に x
1 x     x         x
...
```

行頭の数字は行の頂点番号で、その右に列 0, 1, 2, ... の順に 1 文字ずつ（空白区切りで）並びます。無向グラフなので行列は対角線に対して対称です。

### `ex2_traverse`: グラフの探索

メニュー項目 [2] `graph.ex2_traverse`

無向グラフに対し、頂点 0 を起点にして 3 通りの探索を行い、訪問順を表示します。

- `Traversal.depth_first_rec`: 再帰による深さ優先探索
- `Traversal.depth_first`: スタックによる深さ優先探索
- `Traversal.breadth_first`: キューによる幅優先探索

訪問時の処理はコールバック (`OnVisit`) で渡しており、ここでは `print` を渡しています。再帰版とスタック版は隣接頂点を取り出す順序が異なるため、訪問順が異なることがあります。

出力の形式:

```
0: 1,2,4
...                                  ← 最初にグラフを隣接リストで表示
Visiting by DFS-rec:     013765284
Visiting by DFS (stack): 048567321
Visiting by BFS (queue): 012438576
```

各探索の行末の数字列が頂点の訪問順です（頂点番号が 1 桁なので区切りなしで並びます）。

### `ex3_distances`: 距離と到達可能性

メニュー項目 [3] `graph.ex3_distances`

有向グラフ `data/directed.csv` を読み込んで次を表示します。

- `Distance.breadth_first`: 幅優先探索で、起点（頂点 0 と頂点 1）から各頂点までの最短辺数を求めます。到達できない頂点は `-1` です。
- `Search.depth_first`: 深さ優先探索で、頂点 0 から 7 へ、また 7 から 0 へ到達できるかを真偽値で返します。有向グラフなので、往路と復路で結果が異なることがあります。

出力の形式:

```
Distances from vertex 0: ArraySeq(0, -1, -1, -1, -1, 1, -1, -1)
Distances from vertex 1: ArraySeq(2, 0, -1, 1, -1, 3, 1, 2)
0-7 path: false
7-0 path: true
```

- `ArraySeq` の第 *i* 要素が、起点から頂点 *i* までの最短の辺数です。起点自身は `0`、到達できない頂点は `-1` です。
- `path:` の行は `true`（到達できる）か `false`（到達できない）です。

### `ex4_topological_sort`: トポロジカルソート

メニュー項目 [4] `graph.ex4_topological_sort`

有向グラフ `data/directed.csv` に対して次を行います。

- `Topological.traverse`: 深さ優先探索の過程を、頂点に入る時 (`Nin`) と出る時 (`Nout`) の列として表示します。未訪問の頂点を起点に探索を始め直すたびに 1 行ずつ出力します。
- `Topological.sort`: 帰りがけ順（`out` の順）に頂点を記録して逆順にしたものを返します。グラフが DAG（閉路を持たない）であれば、これがトポロジカル順序になります。

出力の形式:

```
0in -> 5in -> 5out -> 0out
1in -> 3in -> 7in -> 7out -> 3out -> 6in -> 6out -> 1out
2in -> 2out
4in -> 4out
4, 2, 1, 6, 3, 7, 0, 5
```

- `traverse` の各行が 1 回の深さ優先探索に対応します。`Nin` は頂点 N に入った（行きがけ）、`Nout` は N の探索を終えて戻った（帰りがけ）ことを表します。入れ子になった `in` と `out` が、再帰呼び出しの構造に対応します。
- 最終行が `sort` の結果で、トポロジカル順序でのカンマ区切りの頂点列です。辺 u→v があれば u は v より前に現れます。この例では `data/directed.csv` は閉路を持たないため、この順序が成り立ちます。
