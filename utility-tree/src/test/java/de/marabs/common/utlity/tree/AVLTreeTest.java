/*
 * Test of AVLTree class
 * Copyright (C) 2012 Martin Absmeier, IT Consulting Services
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.marabs.common.utlity.tree;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of AVLTree class
 * 
 * @author Martin Absmeier
 */
public class AVLTreeTest extends TestCase {

	@Before
    @Override
	public void setUp() throws Exception {
		// nothing to do
	}
	
    @After
    @Override
	public void tearDown() throws Exception {
		// nothing to do
	}
	
	@Test
	public void testInsert() {
		// this array in this order allows to pass in all branches of the insertion algorithm
		int[] array = { 16, 13, 15, 14, 2, 0, 12, 9, 8, 5, 11, 18, 19, 17, 4, 7, 1, 3, 6, 10 };
		AVLTree<Integer> tree = buildTree(array);

		Assert.assertEquals(array.length, tree.size());

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(array[i], value(tree.getNotSmaller(array[i])));
		}

		checkOrder(tree);
	}

	@Test
	public void testDelete() {
		int[][][] arrays = {	{	{ 16, 13, 15, 14, 2, 0, 12, 9, 8, 5, 11, 18, 19, 17, 4,	7, 1, 3, 6, 10 },
									{ 11, 10, 9, 12, 16, 15, 13, 18, 5, 0, 3, 2, 14, 6, 19,	17, 8, 4, 7, 1 }
								},
								{	{ 16, 13, 15, 14, 2, 0, 12, 9, 8, 5, 11, 18, 19, 17, 4,	7, 1, 3, 6, 10 }, 
									{ 0, 17, 14, 15, 16, 18, 6 } 
								},	
								{	{ 6, 2, 7, 8, 1, 4, 3, 5 }, 
									{ 8 } 
								},
								{	{ 6, 2, 7, 8, 1, 4, 5 }, 
									{ 8 } 
								},
								{	{ 3, 7, 2, 1, 5, 8, 4 }, 
									{ 1 } 
								},
								{	{ 3, 7, 2, 1, 5, 8, 6 }, 
									{ 1 } 
								} 
							};
		
		for (int i = 0; i < arrays.length; ++i) {
			AVLTree<Integer> tree = buildTree(arrays[i][0]);
			Assert.assertTrue(!tree.delete(-2000));
			for (int j = 0; j < arrays[i][1].length; ++j) {
				Assert.assertTrue(tree.delete(tree.getNotSmaller(arrays[i][1][j]).getElement()));
				Assert.assertEquals(arrays[i][0].length - j - 1, tree.size());
			}
		}
	}

	@Test
	public void testNavigation() {
		int[] array = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

		AVLTree<Integer> tree = buildTree(array);

		AVLNode<Integer> node = tree.getSmallest();
		Assert.assertEquals(array[0], value(node));
		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(array[i], value(node));
			node = node.getNext();
		}
		Assert.assertNull(node);

		node = tree.getLargest();
		Assert.assertEquals(array[array.length - 1], value(node));
		for (int i = array.length - 1; i >= 0; --i) {
			Assert.assertEquals(array[i], value(node));
			node = node.getPrevious();
		}
		Assert.assertNull(node);

		checkOrder(tree);
	}

	@Test
	public void testSearch() {
		int[] array = { 2, 4, 6, 8, 10, 12, 14 };
		
		AVLTree<Integer> tree = buildTree(array);

		Assert.assertNull(tree.getNotLarger(array[0] - 1));
		Assert.assertNull(tree.getNotSmaller(array[array.length - 1] + 1));

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(array[i], value(tree.getNotSmaller(array[i] - 1)));
			Assert.assertEquals(array[i], value(tree.getNotLarger(array[i] + 1)));
		}

		checkOrder(tree);
	}

	@Test
	public void testRepetition() {
		int[] array = { 1, 1, 3, 3, 4, 5, 6, 7, 7, 7, 7, 7 };

		AVLTree<Integer> tree = buildTree(array);
		Assert.assertEquals(array.length, tree.size());

		AVLNode<Integer> node = tree.getNotSmaller(3);
		Assert.assertEquals(3, value(node));
		Assert.assertEquals(1, value(node.getPrevious()));
		Assert.assertEquals(3, value(node.getNext()));
		Assert.assertEquals(4, value(node.getNext().getNext()));

		node = tree.getNotLarger(2);
		Assert.assertEquals(1, value(node));
		Assert.assertEquals(1, value(node.getPrevious()));
		Assert.assertEquals(3, value(node.getNext()));
		Assert.assertNull(node.getPrevious().getPrevious());

		AVLNode<Integer> otherNode = tree.getNotSmaller(1);
		Assert.assertTrue(node != otherNode);
		Assert.assertEquals(1, value(otherNode));
		Assert.assertNull(otherNode.getPrevious());

		node = tree.getNotLarger(10);
		Assert.assertEquals(7, value(node));
		Assert.assertNull(node.getNext());
		node = node.getPrevious();
		Assert.assertEquals(7, value(node));
		node = node.getPrevious();
		Assert.assertEquals(7, value(node));
		node = node.getPrevious();
		Assert.assertEquals(7, value(node));
		node = node.getPrevious();
		Assert.assertEquals(7, value(node));
		node = node.getPrevious();
		Assert.assertEquals(6, value(node));

		checkOrder(tree);
	}

	private AVLTree<Integer> buildTree(int[] array) {
		AVLTree<Integer> tree = new AVLTree<Integer>();

		for (int i = 0; i < array.length; ++i) {
			tree.insert(array[i]);
		}

		return tree;
	}

	private int value(AVLNode<Integer> node) {
		return node.getElement();
	}

	private void checkOrder(AVLTree<Integer> tree) {
		AVLNode<Integer> next = null;

		for (AVLNode<Integer> node = tree.getSmallest(); node != null; node = next) {
			next = node.getNext();
			if (next != null) {
				Assert.assertTrue(node.getElement().compareTo(next.getElement()) <= 0);
			}
		}
	}

}
