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
package de.marabs.common.utlity.tree;

import lombok.Data;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * {@code AVLNode} implement all the logical structure of the tree. Nodes are created by the {@link AVLTree} class.<br />
 * The nodes are not independent from each other but must obey specific balancing constraints and the tree structure is
 * rearranged as elements are inserted or deleted from the tree. The creation, modification and tree related navigation
 * methods have therefore restricted access. Only the order-related navigation, reading and delete methods are public.
 *
 * @author Martin Absmeier
 * @see AVLTree
 */
@Data
public class AVLNode<T extends Comparable<T>> {

    /**
     * Element contained in the current node.
     */
    private T element;

    /**
     * Left sub-tree.
     */
    private AVLNode<T> leftNode;

    /**
     * Right sub-tree.
     */
    private AVLNode<T> rightNode;

    /**
     * Parent tree.
     */
    private AVLNode<T> parentNode;

    /**
     * Balance factor.
     */
    private Balance balance;

    /**
     * Build a node for a specified element.
     *
     * @param element element
     * @param parent  parentNode node
     */
    public AVLNode(T element, AVLNode<T> parent) {
        this.element = element;
        this.parentNode = parent;
        this.balance = Balance.BALANCED;
    }

    /**
     * Delete the node from the tree.
     */
    public void delete() {
        if (isLastNode()) {
            // the tree is now empty
            this.element = null;
            return;
        }
        AVLNode<T> node;
        AVLNode<T> child;
        boolean leftShrunk;
        if ((getLeftNode() == null) && (getRightNode() == null)) {
            node = this;
            this.element = null;
            leftShrunk = node == node.getParentNode().getLeftNode();
            child = null;
        } else {
            node = hasLeftNode() ? getLeftNode().getLargest() : getRightNode().getSmallest();
            this.element = node.getElement();
            leftShrunk = node == node.getParentNode().getLeftNode();
            child = (node.getLeftNode() != null) ? node.getLeftNode() : node.getRightNode();
        }

        node = node.getParentNode();
        if (leftShrunk) {
            node.leftNode = child;
        } else {
            node.rightNode = child;
        }
        if (child != null) {
            child.parentNode = node;
        }

        while (leftShrunk ? node.rebalanceLeftShrunk() : node.rebalanceRightShrunk()) {
            if (node.getParentNode() == null) {
                return;
            }
            leftShrunk = node == node.getParentNode().getLeftNode();
            node = node.getParentNode();
        }
    }

    /**
     * Get the node whose element is the largest one in the tree rooted at this node.
     *
     * @return the tree node containing the largest element in the tree rooted at this node or null if the tree is empty
     * @see #getSmallest
     */
    public AVLNode<T> getLargest() {
        AVLNode<T> node = this;
        while (nonNull(node.getRightNode())) {
            node = node.getRightNode();
        }
        return node;
    }

    /**
     * Get the node containing the next larger or equal element.
     *
     * @return node containing the next larger or equal element (in which
     * case the node is guaranteed not to be empty) or null if there
     * is no larger or equal element in the tree
     * @see #getPrevious
     */
    public AVLNode<T> getNext() {
        if (getRightNode() != null) {
            final AVLNode<T> node = getRightNode().getSmallest();
            if (node != null) {
                return node;
            }
        }
        for (AVLNode<T> node = this; node.getParentNode() != null; node = node.getParentNode()) {
            if (node != node.getParentNode().getRightNode()) {
                return node.getParentNode();
            }
        }

        return null;
    }

    /**
     * Get the node containing the next smaller or equal element.
     *
     * @return node containing the next smaller or equal element or null if there is no smaller or equal element in the tree
     * @see #getNext
     */
    public AVLNode<T> getPrevious() {
        if (getLeftNode() != null) {
            final AVLNode<T> node = getLeftNode().getLargest();
            if (node != null) {
                return node;
            }
        }
        for (AVLNode<T> node = this; node.getParentNode() != null; node = node.getParentNode()) {
            if (node != node.getParentNode().getLeftNode()) {
                return node.getParentNode();
            }
        }

        return null;
    }

    /**
     * Get the node whose element is the smallest one in the tree rooted at this node.
     *
     * @return the tree node containing the smallest element in the tree rooted at this node or null if the tree is empty
     * @see #getLargest
     */
    public AVLNode<T> getSmallest() {
        AVLNode<T> node = this;
        while (nonNull(node.getLeftNode())) {
            node = node.getLeftNode();
        }
        return node;
    }

    /**
     * Insert an element in a sub-tree.
     *
     * @param newElement element to insert
     * @return true if the parentNode tree should be re-Balance.BALANCED
     */
    public boolean insert(T newElement) {
        if (newElement.compareTo(getElement()) < 0) {
            // the inserted element is smaller than the node
            if (isNull(getLeftNode())) {
                this.leftNode = new AVLNode<>(newElement, this);
                return rebalanceLeftGrown();
            }
            return getLeftNode().insert(newElement) && rebalanceLeftGrown();
        }

        // the inserted element is equal to or greater than the node
        if (isNull(getRightNode())) {
            this.rightNode = new AVLNode<>(newElement, this);
            return rebalanceRightGrown();
        }

        return getRightNode().insert(newElement) && rebalanceRightGrown();
    }

    /**
     * Re-balance the instance as leftNode subtree has grown.
     *
     * @return true if the parentNode tree should be reSkew.BALANCED too
     */
    private boolean rebalanceLeftGrown() {
        switch (this.balance) {
            case LEFT_HIGH:
                if (getLeftNode().balance == Balance.LEFT_HIGH) {
                    rotateClockwise();
                    this.balance = Balance.BALANCED;
                    getRightNode().balance = Balance.BALANCED;
                } else {
                    final Balance tmpBalance = getLeftNode().rightNode.balance;
                    this.leftNode.rotateCounterClockwise();
                    rotateClockwise();
                    switch (tmpBalance) {
                        case LEFT_HIGH:
                            getLeftNode().balance = Balance.BALANCED;
                            getRightNode().balance = Balance.RIGHT_HIGH;
                            break;
                        case RIGHT_HIGH:
                            getLeftNode().balance = Balance.LEFT_HIGH;
                            getRightNode().balance = Balance.BALANCED;
                            break;
                        default:
                            getLeftNode().balance = Balance.BALANCED;
                            getRightNode().balance = Balance.BALANCED;
                    }
                    this.balance = Balance.BALANCED;
                }
                return false;
            case RIGHT_HIGH:
                this.balance = Balance.BALANCED;
                return false;
            default:
                this.balance = Balance.LEFT_HIGH;
                return true;
        }
    }

    /**
     * Re-balance the instance as leftNode sub-tree has shrunk.
     *
     * @return true if the parentNode tree should be reSkew.BALANCED too
     */
    private boolean rebalanceLeftShrunk() {
        switch (balance) {
            case LEFT_HIGH:
                this.balance = Balance.BALANCED;
                return true;
            case RIGHT_HIGH:
                if (getRightNode().balance == Balance.RIGHT_HIGH) {
                    rotateCounterClockwise();
                    this.balance = Balance.BALANCED;
                    getLeftNode().balance = Balance.BALANCED;
                    return true;
                } else if (getRightNode().balance == Balance.BALANCED) {
                    rotateCounterClockwise();
                    this.balance = Balance.LEFT_HIGH;
                    getLeftNode().balance = Balance.RIGHT_HIGH;
                    return false;
                } else {
                    final Balance tmpBalance = getRightNode().getLeftNode().balance;
                    getRightNode().rotateClockwise();
                    rotateCounterClockwise();
                    switch (tmpBalance) {
                        case LEFT_HIGH:
                            getLeftNode().balance = Balance.BALANCED;
                            getRightNode().balance = Balance.RIGHT_HIGH;
                            break;
                        case RIGHT_HIGH:
                            getLeftNode().balance = Balance.LEFT_HIGH;
                            getRightNode().balance = Balance.BALANCED;
                            break;
                        default:
                            getLeftNode().balance = Balance.BALANCED;
                            getRightNode().balance = Balance.BALANCED;
                    }
                    this.balance = Balance.BALANCED;
                    return true;
                }
            default:
                this.balance = Balance.RIGHT_HIGH;
                return false;
        }
    }

    /**
     * Re-balance the instance as rightNode sub-tree has grown.
     *
     * @return true if the parentNode tree should be reSkew.BALANCED too
     */
    private boolean rebalanceRightGrown() {
        switch (balance) {
            case LEFT_HIGH:
                this.balance = Balance.BALANCED;
                return false;
            case RIGHT_HIGH:
                if (getRightNode().balance == Balance.RIGHT_HIGH) {
                    rotateCounterClockwise();
                    this.balance = Balance.BALANCED;
                    getLeftNode().balance = Balance.BALANCED;
                } else {
                    final Balance tmpBalance = getRightNode().getLeftNode().balance;
                    getRightNode().rotateClockwise();
                    rotateCounterClockwise();
                    switch (tmpBalance) {
                        case LEFT_HIGH:
                            getLeftNode().balance = Balance.BALANCED;
                            getRightNode().balance = Balance.RIGHT_HIGH;
                            break;
                        case RIGHT_HIGH:
                            getLeftNode().balance = Balance.LEFT_HIGH;
                            getRightNode().balance = Balance.BALANCED;
                            break;
                        default:
                            getLeftNode().balance = Balance.BALANCED;
                            getRightNode().balance = Balance.BALANCED;
                    }
                    this.balance = Balance.BALANCED;
                }
                return false;
            default:
                this.balance = Balance.RIGHT_HIGH;
                return true;
        }
    }

    /**
     * Re-balance the instance as rightNode sub-tree has shrunk.
     *
     * @return true if the parentNode tree should be reSkew.BALANCED too
     */
    private boolean rebalanceRightShrunk() {
        switch (balance) {
            case RIGHT_HIGH:
                this.balance = Balance.BALANCED;
                return true;
            case LEFT_HIGH:
                if (getLeftNode().balance == Balance.LEFT_HIGH) {
                    rotateClockwise();
                    this.balance = Balance.BALANCED;
                    getRightNode().balance = Balance.BALANCED;
                    return true;
                } else if (getLeftNode().balance == Balance.BALANCED) {
                    rotateClockwise();
                    this.balance = Balance.RIGHT_HIGH;
                    getRightNode().balance = Balance.LEFT_HIGH;
                    return false;
                } else {
                    final Balance tmpBalance = getLeftNode().getRightNode().balance;
                    this.leftNode.rotateCounterClockwise();
                    rotateClockwise();
                    switch (tmpBalance) {
                        case LEFT_HIGH:
                            getLeftNode().balance = Balance.BALANCED;
                            getRightNode().balance = Balance.RIGHT_HIGH;
                            break;
                        case RIGHT_HIGH:
                            getLeftNode().balance = Balance.LEFT_HIGH;
                            getRightNode().balance = Balance.BALANCED;
                            break;
                        default:
                            getLeftNode().balance = Balance.BALANCED;
                            getRightNode().balance = Balance.BALANCED;
                    }
                    this.balance = Balance.BALANCED;
                    return true;
                }
            default:
                this.balance = Balance.LEFT_HIGH;
                return false;
        }
    }

    /**
     * Get the number of elements of the tree rooted at this node.
     *
     * @return number of elements contained in the tree rooted at this node
     */
    public int size() {
        return 1 + ((getLeftNode() == null) ? 0 : getLeftNode().size()) + ((getRightNode() == null) ? 0 : getRightNode().size());
    }

    // #################################################################################################################

    /**
     * Perform a clockwise rotation rooted at the instance. The balance factor are not updated by this method,
     * they <b>must</b> be updated by the caller.
     */
    private void rotateClockwise() {
        T tmpElement = getElement();
        this.element = getLeftNode().getElement();
        getLeftNode().element = tmpElement;

        AVLNode<T> tmpNode = getLeftNode();
        this.leftNode = tmpNode.getLeftNode();
        tmpNode.leftNode = tmpNode.getRightNode();
        tmpNode.rightNode = getRightNode();
        this.rightNode = tmpNode;

        if (nonNull(getLeftNode())) {
            getLeftNode().parentNode = this;
        }
        if (nonNull(getRightNode().getRightNode())) {
            getRightNode().getRightNode().parentNode = getRightNode();
        }
    }

    /**
     * Perform a counter-clockwise rotation rooted at the instance. The balance factor are not updated by this method,
     * they <b>must</b> be updated by the caller.
     */
    private void rotateCounterClockwise() {
        T tmpElement = getElement();
        this.element = getRightNode().getElement();
        getRightNode().element = tmpElement;

        AVLNode<T> tmpNode = getRightNode();
        this.rightNode = tmpNode.getRightNode();
        tmpNode.rightNode = tmpNode.getLeftNode();
        tmpNode.leftNode = getLeftNode();
        this.leftNode = tmpNode;

        if (nonNull(getRightNode())) {
            getRightNode().parentNode = this;
        }
        if (nonNull(getLeftNode().getLeftNode())) {
            getLeftNode().getLeftNode().parentNode = getLeftNode();
        }
    }

    private boolean isLastNode() {
        return isNull(getParentNode()) && isNull(getLeftNode()) && isNull(getRightNode());
    }

    private boolean hasParent() {
        return nonNull(getParentNode());
    }

    private boolean hasLeftNode() {
        return nonNull(getLeftNode());
    }

    private boolean hasRightNode() {
        return nonNull(getRightNode());
    }
}