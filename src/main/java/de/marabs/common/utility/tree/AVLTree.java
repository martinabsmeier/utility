/*
 * Copyright 2022 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.marabs.common.utility.tree;

/**
 * This class implements AVL trees.
 * 
 * <p>
 * The purpose of this class is to sort elements while allowing duplicate
 * elements (i.e. such that {@code a.equals(b)} is true). The {@code SortedSet}
 * interface does not allow this, so a specific class is needed. Null elements
 * are not allowed.
 * </p>
 * 
 * <p>
 * Since the {@code equals} method is not sufficient to differentiate elements,
 * the {@link #delete delete} method is implemented using the equality operator.
 * </p>
 * 
 * <p>
 * In order to clearly mark the methods provided here do not have the same
 * semantics as the ones specified in the {@code SortedSet} interface, different
 * names are used ({@code add} has been replaced by {@link #insert insert} and
 * {@code remove} has been replaced by {@link #delete delete}).
 * </p>
 * 
 * <p>
 * This class is based on the C implementation Georg Kraml has put in the public
 * domain. Unfortunately, his <a href="www.purists.org/georg/avltree/index.html">page</a> 
 * seems not to exist any more.
 * </p>
 * 
 * @param <T>
 *            the type of the elements
 * @author Martin Absmeier
 */
public class AVLTree<T extends Comparable<T>> {

	/** Top level node. */
	private AVLNode<T> rootNode;

	/**
	 * Build an empty tree.
	 */
	public AVLTree() {
		this.rootNode = null;
	}

	/**
	 * Delete an element from the tree.
	 * <p>
	 * The element is deleted only if there is a node {@code n} containing
	 * exactly the element instance specified, i.e. for which
	 * {@code n.getElement() == element}. This is purposely <em>different</em>
	 * from the specification of the {@code java.util.Set} {@code remove} method
	 * (in fact, this is the reason why a specific class has been developed).
	 * </p>
	 * 
	 * @param element
	 *            element to delete (silently ignored if null)
	 * @return true if the element was deleted from the tree
	 */
	public boolean delete(final T element) {
		if (element != null) {
			for (AVLNode<T> node = getNotSmaller(element); node != null; node = node.getNext()) {
				// loop over all elements neither smaller nor larger than the specified one
				if (node.getElement().equals(element)) {
					node.delete();
					if(	this.rootNode.getLeftNode() == null && 
						this.rootNode.getParentNode() == null && 
						this.rootNode.getRightNode() == null &&
						this.rootNode.getElement() == null)
					{
						this.rootNode = null;
					}
					return true;
				} else if (node.getElement().compareTo(element) > 0) {
					// all the remaining elements are known to be larger, the element is not in the tree
					return false;
				}
			}
		}

		return false;
	}

	/**
	 * Get the node whose element is the largest one in the tree.
	 * 
	 * @return the tree node containing the largest element in the tree or null
	 *         if the tree is empty
	 * @see #getSmallest
	 * @see #getNotSmaller
	 * @see #getNotLarger
	 * @see AVLNode#getPrevious
	 * @see AVLNode#getNext
	 */
	public AVLNode<T> getLargest() {
		return (this.rootNode == null) ? null : this.rootNode.getLargest();
	}

	/**
	 * Get the node whose element is not larger than the reference object.
	 * 
	 * @param reference
	 *            reference object (may not be in the tree)
	 * @return the tree node containing the largest element not larger than the
	 *         reference object (in which case the node is guaranteed not to be
	 *         empty) or null if either the tree is empty or all its elements
	 *         are larger than the reference object
	 * @see #getSmallest
	 * @see #getLargest
	 * @see #getNotSmaller
	 * @see AVLNode#getPrevious
	 * @see AVLNode#getNext
	 */
	public AVLNode<T> getNotLarger(final T reference) {
		AVLNode<T> candidate = null;
		for (AVLNode<T> node = this.rootNode; node != null;) {
			if (node.getElement().compareTo(reference) > 0) {
				if (node.getLeftNode() == null) {
					return candidate;
				}
				node = node.getLeftNode();
			} else {
				candidate = node;
				if (node.getRightNode() == null) {
					return candidate;
				}
				node = node.getRightNode();
			}
		}

		return null;
	}

	/**
	 * Get the node whose element is not smaller than the reference object.
	 * 
	 * @param reference
	 *            reference object (may not be in the tree)
	 * @return the tree node containing the smallest element not smaller than
	 *         the reference object or null if either the tree is empty or all
	 *         its elements are smaller than the reference object
	 * @see #getSmallest
	 * @see #getLargest
	 * @see #getNotLarger
	 * @see AVLNode#getPrevious
	 * @see AVLNode#getNext
	 */
	public AVLNode<T> getNotSmaller(final T reference) {
		AVLNode<T> candidate = null;
		for (AVLNode<T> node = rootNode; node != null;) {
			if (node.getElement().compareTo(reference) < 0) {
				if (node.getRightNode() == null) {
					return candidate;
				}
				node = node.getRightNode();
			} else {
				candidate = node;
				if (node.getLeftNode() == null) {
					return candidate;
				}
				node = node.getLeftNode();
			}
		}
		return null;
	}

	/**
	 * Get the node whose element is the smallest one in the tree.
	 * 
	 * @return the tree node containing the smallest element in the tree or null
	 *         if the tree is empty
	 * @see #getLargest
	 * @see #getNotSmaller
	 * @see #getNotLarger
	 * @see AVLNode#getPrevious
	 * @see AVLNode#getNext
	 */
	public AVLNode<T> getSmallest() {
		return (this.rootNode == null) ? null : this.rootNode.getSmallest();
	}

	/**
	 * Insert an element in the tree.
	 * 
	 * @param newElement
	 *            element to insert (silently ignored if null)
	 */
	public void insert(final T newElement) {
		if (newElement != null) {
			if (this.rootNode == null) {
				this.rootNode = new AVLNode<T>(newElement, null);
			} else {
				this.rootNode.insert(newElement);
			}
		}
	}

	/**
	 * Check if the tree is empty.
	 * 
	 * @return true if the tree is empty
	 */
	public boolean isEmpty() {
		return this.rootNode == null;
	}

	/**
	 * Get the number of elements of the tree.
	 * 
	 * @return number of elements contained in the tree
	 */
	public int size() {
		return (this.rootNode == null) ? 0 : rootNode.size();
	}

}
