package algorithms;

import structures.CustomArrayList;

/*
 * Algorithm: Aho-Corasick multi-pattern string matching automaton
 * Complexity: O(n + Σ|patterns| + total_matches)
 *
 * How it works:
 *  1. Build a Trie from all pattern keywords.
 *  2. Compute failure links via BFS (like KMP failure function but on a Trie).
 *  3. Scan the text once; follow goto/failure transitions to find all patterns
 *     simultaneously in a single pass.
 *
 * Returns the number of keyword matches found in the text.
 */
public class AhoCorasick {

    // ---- Trie node ----
    private static class Node {
        int[] children;
        int failLink;
        boolean isEnd;
        String keyword;

        Node() {
            children = new int[26];
            java.util.Arrays.fill(children, -1);
            failLink = 0;
            isEnd = false;
            keyword = null;
        }
    }

    private CustomArrayList<Node> trie;
    private long comparisons;
    private boolean built;

    public AhoCorasick() {
        trie = new CustomArrayList<>();
        trie.add(new Node()); // root = node 0
        comparisons = 0;
        built = false;
    }

    public void resetComparisons() { comparisons = 0; }
    public long getComparisons() { return comparisons; }

    /** Add a keyword to the automaton (must call buildFailLinks() after all addPattern calls). */
    public void addPattern(String keyword) {
        if (keyword == null || keyword.isEmpty()) return;
        String p = keyword.toLowerCase();
        int cur = 0;
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if (c < 'a' || c > 'z') continue; // skip non-alpha
            int idx = c - 'a';
            if (trie.get(cur).children[idx] == -1) {
                trie.get(cur).children[idx] = trie.size();
                trie.add(new Node());
            }
            cur = trie.get(cur).children[idx];
        }
        trie.get(cur).isEnd = true;
        trie.get(cur).keyword = p;
        built = false;
    }

    /** Build failure links using BFS. Call this once after all addPattern() calls. */
    public void buildFailLinks() {
        // BFS queue (simple array-backed queue)
        int[] queue = new int[trie.size()];
        int head = 0, tail = 0;

        Node root = trie.get(0);
        for (int c = 0; c < 26; c++) {
            if (root.children[c] == -1) {
                root.children[c] = 0; // missing → loop back to root
            } else {
                trie.get(root.children[c]).failLink = 0;
                queue[tail++] = root.children[c];
            }
        }

        while (head < tail) {
            int u = queue[head++];
            Node uNode = trie.get(u);
            for (int c = 0; c < 26; c++) {
                int v = uNode.children[c];
                if (v == -1) {
                    uNode.children[c] = trie.get(uNode.failLink).children[c];
                } else {
                    trie.get(v).failLink = trie.get(uNode.failLink).children[c];
                    // Propagate output (dictionary links)
                    if (trie.get(trie.get(v).failLink).isEnd && !trie.get(v).isEnd) {
                        // no-op here; we propagate on search
                    }
                    queue[tail++] = v;
                }
            }
        }
        built = true;
    }

    /**
     * Returns the set of keywords that appear in text.
     * Call buildFailLinks() before this.
     */
    public CustomArrayList<String> search(String text) {
        CustomArrayList<String> found = new CustomArrayList<>();
        if (!built || text == null || text.isEmpty()) return found;

        String t = text.toLowerCase();
        int cur = 0;

        for (int i = 0; i < t.length(); i++) {
            char ch = t.charAt(i);
            if (ch < 'a' || ch > 'z') { cur = 0; continue; }
            int c = ch - 'a';
            comparisons++;
            cur = trie.get(cur).children[c];

            // Check this node and all failure-link nodes for matches
            int temp = cur;
            while (temp != 0) {
                if (trie.get(temp).isEnd) {
                    String kw = trie.get(temp).keyword;
                    if (!found.contains(kw)) found.add(kw);
                }
                temp = trie.get(temp).failLink;
                comparisons++;
            }
        }
        return found;
    }

    /**
     * Convenience: returns true if ANY of the keywords appear in text.
     */
    public boolean containsAny(String text) {
        return search(text).size() > 0;
    }
}
