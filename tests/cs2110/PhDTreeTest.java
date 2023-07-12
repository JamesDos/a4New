package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

public class PhDTreeTest {

    // These pre-defined Professor and PhDTree objects may be used to simplify the setup for your
    // test cases.  You are encouraged to add your own helper methods (even `tree3()` would be
    // considered "trivial", since no node has more than 1 child).
    private static final Professor prof1 = new Professor("Amy Huang", 2023);
    private static final Professor prof2 = new Professor("Maya Leong", 2023);
    private static final Professor prof3 = new Professor("Matthew Hui", 2025);
    private static final Professor prof4 = new Professor("Arianna Curillo", 2022);
    private static final Professor prof5 = new Professor("Michelle Gao", 2022);
    private static final Professor prof6 = new Professor("Isa Siu", 2024);
    private static final Professor prof7 = new Professor("prof7", 2027);

    private static final Professor prof8 = new Professor("Curran_Muhlberger", 2027);
    private static final Professor prof9 = new Professor("Tomer_Shamir", 2027);
    private static final Professor prof10 = new Professor("David_Gries", 2027);


    // These helper methods create a copy of each Professor object, which would normally be seen as
    // wasteful.  They do so to help expose bugs involving the use of `==` instead of `.equals()`.
    private static PhDTree tree1() {
        return new PhDTree(new Professor(prof1));
    }

    private static PhDTree tree2() {
        return new PhDTree(new Professor(prof4));
    }

    private static PhDTree tree3() throws NotFound {
        PhDTree t = new PhDTree(new Professor(prof1));
        t.insert(prof1.name(), new Professor(prof2));
        t.insert(prof2.name(), new Professor(prof3));
        return t;
    }

    // Additional Trees

    // Tree of height 2; root has 1 advisee
    private static PhDTree tree4() throws NotFound{
        PhDTree t = new PhDTree(new Professor(prof1));
        t.insert(prof1.name(), new Professor(prof2));
        t.insert(prof1.name(), new Professor(prof3));
        return t;
    }

    // Tree of height 2; root has > 1 advisees
    private static PhDTree tree5() throws NotFound{
        PhDTree t = new PhDTree(new Professor(prof1));
        t.insert(prof1.name(), new Professor(prof2));
        t.insert(prof1.name(), new Professor(prof3));
        t.insert(prof1.name(), new Professor(prof4));
        return t;
    }

    // Tree of height 3; root has > 1 advisees; advisees has > 1 advisee;
    private static PhDTree tree6() throws NotFound{
        PhDTree t = new PhDTree(new Professor(prof1));
        t.insert(prof1.name(), new Professor(prof2));
        t.insert(prof1.name(), new Professor(prof3));
        t.insert(prof2.name(), new Professor(prof4));
        t.insert(prof2.name(), new Professor(prof5));
        t.insert(prof3.name(), new Professor(prof6));
        t.insert(prof3.name(), new Professor(prof7));
        return t;
    }

    // Tree of height 4; root has > 1 advisees; unbalanced branches; largest branch on left
    private static PhDTree tree7() throws NotFound{
        PhDTree t = new PhDTree(new Professor(prof1));
        t.insert(prof1.name(), new Professor(prof2));
        t.insert(prof1.name(), new Professor(prof3));
        t.insert(prof2.name(), new Professor(prof4));
        t.insert(prof2.name(), new Professor(prof5));
        t.insert(prof4.name(), new Professor(prof6));
        t.insert(prof4.name(), new Professor(prof7));
        return t;
    }

    // Tree of height 4; root has > 1 advisees; unbalanced branches; largest branch on right
    private static PhDTree tree8() throws NotFound{
        PhDTree t = new PhDTree(new Professor(prof1));
        t.insert(prof1.name(), new Professor(prof3));
        t.insert(prof1.name(), new Professor(prof2));
        t.insert(prof3.name(), new Professor(prof4));
        t.insert(prof3.name(), new Professor(prof5));
        t.insert(prof5.name(), new Professor(prof6));
        t.insert(prof5.name(), new Professor(prof7));
        return t;
    }

    // Tree of height 3; root has > 1 advisees; unbalanced branches; largest branch in the middle
    private static PhDTree tree9() throws NotFound{
        PhDTree t = new PhDTree(new Professor(prof1));
        t.insert(prof1.name(), new Professor(prof2));
        t.insert(prof1.name(), new Professor(prof3));
        t.insert(prof1.name(), new Professor(prof4));
        t.insert(prof2.name(), new Professor(prof5));
        return t;
    }

    // Tree from handout
    private static PhDTree handoutTree() throws NotFound{
        PhDTree t = new PhDTree(new Professor(prof2));
        t.insert(prof2.name(), new Professor(prof3));
        t.insert(prof3.name(), new Professor(prof1));
        t.insert(prof1.name(), new Professor(prof10));
        t.insert(prof2.name(), new Professor(prof8));
        t.insert(prof8.name(), new Professor(prof9));
        return t;
    }


    @Test
    public void testConstructorProfToString() {
        PhDTree t1 = tree1();
        assertEquals("Amy Huang", t1.toString());
        assertEquals(prof1, t1.prof());

        PhDTree t2 = tree2();
        assertEquals("Arianna Curillo", t2.toString());
        assertEquals(prof4, t2.prof());
    }

    @Test
    public void testNumAdvisees() throws NotFound {
        PhDTree t = tree1();
        assertEquals(0, t.numAdvisees());
        // TODO: Add three additional tests of `numAdvisees()` using your own tree(s)

        // Root has 1 direct advisee; height = 2
        t = tree3();
        assertEquals(1, t.numAdvisees());
        // Root has > 1 direct advisee; each advisee has > 1 advisee; height = 2
        t = tree4();
        assertEquals(2, t.numAdvisees());
        // Root has > 1 direct advisee; height = 2
        t = tree5();
        assertEquals(3, t.numAdvisees());
        t = tree8();
        assertEquals(2, t.numAdvisees());

    }

    @Test
    public void testSize() throws NotFound {
        PhDTree t = tree3();
        assertEquals(3, t.size());
        // TODO: Add three additional tests of `size()` using your own tree(s)

        // Trivial Tree; prof is only node
        t = tree1();
        assertEquals(1, t.size());
        // height = 2
        t = tree5();
        assertEquals(4, t.size());
        //height = 3; balanced branches
        t = tree6();
        assertEquals(7, t.size());
        // height = 4; unbalanced branches; largest branch on the left
        t = tree7();
        assertEquals(7, t.size());
        // height = 4; unbalanced branches; largest branch on the right
        t = tree8();
        assertEquals(7, t.size());
        // height = 4; unbalanced branches; largest branch on the right
        t = tree9();
        assertEquals(5, t.size());
    }

    @Test
    public void testMaxDepth() throws NotFound {
        PhDTree t = tree3();
        assertEquals(3, t.maxDepth());

        // TODO: Add three additional tests of `maxDepth()` using your own tree(s)
        // Trivial Tree; prof is only node
        t = tree1();
        assertEquals(1, t.maxDepth());
        // height = 2
        t = tree5();
        assertEquals(2, t.maxDepth());
        //height = 3; balanced branches
        t = tree6();
        assertEquals(3, t.maxDepth());
        // height = 4; unbalanced branches; largest branch on the left
        t = tree7();
        assertEquals(4, t.maxDepth());
        // height = 4; unbalanced branches; largest branch on the right
        t = tree8();
        assertEquals(4, t.maxDepth());
        // height = 4; unbalanced branches; largest branch on the right
        t = tree9();
        assertEquals(3, t.maxDepth());
    }

    @Test
    public void testFindTree() throws NotFound {
        PhDTree tree1 = tree1();
        tree1.insert(prof1.name(), prof2);
        tree1.insert(prof2.name(), prof3);
        PhDTree tree4 = new PhDTree(prof2);
        tree4.insert(prof2.name(), prof3);
        assertEquals(tree4.prof(), tree1.findTree(prof2.name()).prof());
        assertEquals("Maya Leong[Matthew Hui]", tree1.findTree(prof2.name()).toString());

        assertThrows(NotFound.class, () -> tree2().findTree(prof5.name()));
        assertThrows(NotFound.class, () -> tree1.findTree(prof4.name()));
        assertEquals(1, tree1.findTree(prof3.name()).size());


        // TODO: Add three additional tests of `findTree()` using your own tree(s)

        // Tree only contains root
        final PhDTree t0 = tree1();
        assertEquals(t0.prof(), t0.findTree(prof1.name()).prof());
        assertEquals("Amy Huang", t0.findTree(prof1.name()).toString());
        assertThrows(NotFound.class, () -> t0.findTree(prof5.name()));
        assertEquals(1, t0.findTree(prof1.name()).size());

        // Returned subtree is the entire tree (root has children)
        final PhDTree t = tree3();
        assertEquals(t.prof(), t.findTree(prof1.name()).prof());
        assertEquals("Amy Huang[Maya Leong[Matthew Hui]]", t.findTree(prof1.name()).toString());
        assertThrows(NotFound.class, () -> t.findTree(prof5.name()));
        assertEquals(3, t.findTree(prof1.name()).size());

        // Returned subtree is a leaf
        final PhDTree t1 = tree8();
        PhDTree subtree = new PhDTree(prof6);
        assertEquals(subtree.prof(), t1.findTree(prof6.name()).prof());
        assertEquals("Isa Siu", t1.findTree(prof6.name()).toString());
        assertEquals(1, t1.findTree(prof6.name()).size());

        // Returned subtree is a subtree of a subtree;
        subtree = new PhDTree(prof5);
        subtree.insert(prof5.name(), prof6);
        subtree.insert(prof5.name(), prof7);
        assertEquals(subtree.prof(), t1.findTree(prof5.name()).prof());
        assertEquals("Michelle Gao[Isa Siu, prof7]", t1.findTree(prof5.name()).toString());
        assertEquals(3, t1.findTree(prof5.name()).size());

    }

    @Test
    public void containsTest() throws NotFound {
        PhDTree t = tree3();
        assertTrue(t.contains("Amy Huang"));
        assertFalse(t.contains(prof6.name()));

        // Tree with only root
        PhDTree t1 = tree1();
        assertTrue(t1.contains("Amy Huang"));
        assertFalse(t1.contains(prof6.name()));

        // Tree after inserting a prof
        t1 = tree1();
        t1.insert(prof1.name(), prof2);
        assertTrue(t1.contains("Maya Leong"));

        // Inserting multiple profs to the same prof
        t1.insert(prof1.name(), prof4);
        assertTrue(t1.contains("Arianna Curillo"));
        System.out.println(t1);
        assertTrue(t1.contains("Maya Leong"));

        // Inserting a prof to an inserted prof
        t1.insert(prof2.name(), prof3);
        assertTrue(t1.contains("Matthew Hui"));

    }

    @Test
    public void testInsert() throws NotFound {
        PhDTree t = tree1();
        t.insert(prof1.name(), prof2);
        t.insert(prof2.name(), prof3);
        assertEquals("Amy Huang[Maya Leong[Matthew Hui]]", t.toString());

        // TODO: Add three additional tests of `insert()` using your own tree(s)
        t = tree4();
        assertEquals("Amy Huang[Maya Leong, Matthew Hui]", t.toString());

        t = tree5();
        assertEquals("Amy Huang[Arianna Curillo, Maya Leong, Matthew Hui]", t.toString());

        // test different orders of insertion (recreating tree 5 with different order of insertion)
        t = tree1();
        t.insert(prof1.name(), new Professor(prof4));
        t.insert(prof1.name(), new Professor(prof3));
        t.insert(prof1.name(), new Professor(prof2));
        assertEquals("Amy Huang[Arianna Curillo, Maya Leong, Matthew Hui]", t.toString());

        t = tree6();
        assertEquals("Amy Huang[Maya Leong[Arianna Curillo, Michelle Gao], "
                + "Matthew Hui[Isa Siu, prof7]]", t.toString());

        t = tree7();
        assertEquals("Amy Huang[Maya Leong[Arianna Curillo[Isa Siu, prof7], Michelle Gao], "
                + "Matthew Hui]", t.toString());

        t = tree8();
        assertEquals("Amy Huang[Maya Leong, Matthew Hui[Arianna Curillo, "
                + "Michelle Gao[Isa Siu, prof7]]]", t.toString());

        t = tree9();
        assertEquals("Amy Huang[Arianna Curillo, Maya Leong[Michelle Gao], Matthew Hui]",
                t.toString());
    }

    @Test
    public void testFindAdvisor() throws NotFound {
        PhDTree t = tree3();
        assertEquals(prof2, t.findAdvisor(prof3.name()));
        assertThrows(NotFound.class, () -> t.findAdvisor(prof1.name()));

        // TODO: Add three additional tests of `findAdvisor()` using your own tree(s)

        // Trivial Tree (tree has only root); cannot find own advisor
        PhDTree t1 = tree1();
        assertThrows(NotFound.class, () -> t1.findAdvisor(prof1.name()));

        // Non-Trivial Tree with Balances Branches
        PhDTree t6 = tree6();
        assertEquals(prof1, t6.findAdvisor(prof2.name()));
        assertEquals(prof1, t6.findAdvisor(prof3.name()));
        assertEquals(prof2, t6.findAdvisor(prof4.name()));
        assertEquals(prof3, t6.findAdvisor(prof7.name()));
        // Does not return node from other branch
        assertThrows(NotFound.class, () -> t6.findAdvisor(prof1.name()));

        //TODO: Not sure whether or not test below should throw Notfound
        //Even though technically prof3 is the parent of prof7, the implementation of findAdvisor
        //returns Notfound if tree does not contain prof3
        /**
        PhDTree t6Subtree = t6.findTree(prof7.name());
        System.out.println(t6Subtree);
        assertEquals(prof3, t6Subtree.findAdvisor(prof7.name()));
        // Cannot return grandparent node
        assertThrows(NotFound.class, () -> t6Subtree.findAdvisor(prof1.name()));
         */

        PhDTree t7 = tree7();
        assertEquals(prof1, t7.findAdvisor(prof2.name()));
        assertEquals(prof1, t7.findAdvisor(prof3.name()));
        assertEquals(prof2, t7.findAdvisor(prof4.name()));
        assertEquals(prof2, t7.findAdvisor(prof5.name()));
        assertEquals(prof4, t7.findAdvisor(prof6.name()));
        assertEquals(prof4, t7.findAdvisor(prof7.name()));
        // Cannot return gradparent node
        assertNotEquals(prof2, t7.findAdvisor(prof6.name()));
        //Method should still work after making a subtree (ensures pointers remain)
        PhDTree t7Subtree = t7.findTree(prof2.name());
        assertEquals(prof2, t7Subtree.findAdvisor(prof4.name()));
        assertEquals(prof2, t7Subtree.findAdvisor(prof5.name()));
        assertEquals(prof4, t7Subtree.findAdvisor(prof6.name()));

        // Example tree from handout plus example test cases
        PhDTree Maya_Leong = handoutTree();
        assertThrows(NotFound.class, () -> Maya_Leong.findAdvisor(prof2.name()));
        assertEquals(prof8, Maya_Leong.findAdvisor(prof9.name()));
        PhDTree Curran_Muhlberger = Maya_Leong.findTree(prof8.name());
        assertEquals(prof8, Curran_Muhlberger.findAdvisor(prof9.name()));
        assertEquals(prof2, Maya_Leong.findAdvisor(prof3.name()));
        PhDTree Amy_Huang = Maya_Leong.findTree(prof1.name());
        assertThrows(NotFound.class, () -> Amy_Huang.findAdvisor(prof9.toString()));
        PhDTree Matthew_Hui = Maya_Leong.findTree(prof3.name());
        assertThrows(NotFound.class, () -> Matthew_Hui.findAdvisor(prof3.name()));

    }

    @Test
    public void testFindAcademicLineage() throws NotFound {
        PhDTree t = tree3();
        List<Professor> lineage1 = new LinkedList<>();
        lineage1.add(prof1);
        lineage1.add(prof2);
        lineage1.add(prof3);
        assertEquals(lineage1, t.findAcademicLineage(prof3.name()));

        // TODO: Add three additional tests of `findAcademicLineage()` using your own tree(s)

        // Tree contains only root
        PhDTree t1 = tree1();
        List<Professor> t1Lineage = new LinkedList<>();
        t1Lineage.add(prof1);
        assertEquals(t1Lineage, t1.findAcademicLineage(prof1.name()));

        // Testing each traversal in tree 7: traversal 1
        PhDTree t7 = tree7();
        List<Professor> t7Lineage = new LinkedList<>();
        t7Lineage.add(prof1);
        t7Lineage.add(prof2);
        t7Lineage.add(prof4);
        t7Lineage.add(prof6);
        assertEquals(t7Lineage, t7.findAcademicLineage(prof6.name()));
        // Traversal 2
        t7Lineage.remove(prof6);
        t7Lineage.add(prof7);
        assertEquals(t7Lineage, t7.findAcademicLineage(prof7.name()));
        // Traversal 3 (Traversal stops before a leaf)
        t7Lineage.remove(prof7);
        assertEquals(t7Lineage, t7.findAcademicLineage(prof4.name()));
        // Traversal 4
        t7Lineage.remove(prof4);
        t7Lineage.add(prof5);
        assertEquals(t7Lineage, t7.findAcademicLineage(prof5.name()));
        // Traversal 5
        t7Lineage.remove(prof5);
        t7Lineage.remove(prof2);
        t7Lineage.add(prof3);
        assertEquals(t7Lineage, t7.findAcademicLineage(prof3.name()));
    }

    @Test
    public void testCommonAncestor() throws NotFound {
        PhDTree t = tree3();
        assertEquals(prof2, t.commonAncestor(prof2.name(), prof3.name()));
        assertEquals(prof1, t.commonAncestor(prof1.name(), prof3.name()));
        assertThrows(NotFound.class, () -> t.commonAncestor(prof5.name(), prof3.name()));

        // TODO: Add three additional tests of `commonAncestor()` using your own tree(s)

    }

    @Test
    public void testPrintProfessors() throws NotFound {
        {  // Restrict scope to one test case
            PhDTree t = tree3();

            // A StringWriter lets us capture output that might normally be written to a file, or
            // printed on the console, in a String instead.
            StringWriter out = new StringWriter();

            // Need to wrap our Writer in a PrintWriter to satisfy `printProfessors()` (but we save
            // the original StringWriter so we can access its string later).  Flush the PrintWriter
            // when we are done with it.
            PrintWriter pw = new PrintWriter(out);
            t.printProfessors(pw);
            pw.flush();

            // Split string into lines for easy comparison ("\\R" is a "regular expression" that
            // matches both Windows and Unix line separators; it only works in methods like
            // `split()`).
            String[] lines = out.toString().split("\\R");
            String[] expected = {
                    "Amy Huang - 2023",
                    "Maya Leong - 2023",
                    "Matthew Hui - 2025"
            };
            assertArrayEquals(expected, lines);
        }

        // TODO: Add three additional tests of `printProfessors()` using your own tree(s)
        // Feel free to define a helper method to avoid duplicated testing code.

    }
}
