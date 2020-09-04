/*
 * Autopsy
 *
 * Copyright 2019-2020 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.discovery;

import com.google.common.eventbus.EventBus;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.sleuthkit.autopsy.discovery.FileSearch.GroupKey;
import org.sleuthkit.autopsy.discovery.FileSearchData.FileType;
import org.sleuthkit.datamodel.AbstractFile;

/**
 * Class to handle event bus and events for discovery tool.
 */
final class DiscoveryEventUtils {

    private final static EventBus discoveryEventBus = new EventBus();

    /**
     * Get the discovery event bus.
     *
     * @return The discovery event bus.
     */
    static EventBus getDiscoveryEventBus() {
        return discoveryEventBus;
    }

    /**
     * Private no arg constructor for Utility class.
     */
    private DiscoveryEventUtils() {
        //Utility class private constructor intentionally left blank.
    }

    /**
     * Event to signal the start of a search being performed.
     */
    static final class SearchStartedEvent {

        private final FileType fileType;

        /**
         * Construct a new SearchStartedEvent
         *
         * @param type The type of file the search event is for.
         */
        SearchStartedEvent(FileType type) {
            this.fileType = type;
        }

        /**
         * Get the type of file the search is being performed for.
         *
         * @return The type of files being searched for.
         */
        FileType getType() {
            return fileType;
        }
    }

    /**
     * Event to signal that the Instances list should have selection cleared.
     */
    static final class ClearInstanceSelectionEvent {

        /**
         * Construct a new ClearInstanceSelectionEvent.
         */
        ClearInstanceSelectionEvent() {
            //no arg constructor
        }
    }

    /**
     * Event to signal that the Instances list should be populated.
     */
    static final class PopulateInstancesListEvent {

        private final List<AbstractFile> instances;

        /**
         * Construct a new PopulateInstancesListEvent.
         */
        PopulateInstancesListEvent(List<AbstractFile> files) {
            instances = files;
        }

        /**
         * @return the instances
         */
        List<AbstractFile> getInstances() {
            return Collections.unmodifiableList(instances);
        }
    }

    /**
     * Event to signal the completion of a search being performed.
     */
    static final class SearchCompleteEvent {

        private final Map<GroupKey, Integer> groupMap;
        private final List<FileSearchFiltering.FileFilter> searchFilters;
        private final FileSearch.AttributeType groupingAttribute;
        private final FileGroup.GroupSortingAlgorithm groupSort;
        private final FileSorter.SortingMethod fileSortMethod;

        /**
         * Construct a new SearchCompleteEvent,
         *
         * @param groupMap          The map of groups which were found by the
         *                          search.
         * @param searchFilters     The search filters which were used by the
         *                          search.
         * @param groupingAttribute The grouping attribute used by the search.
         * @param groupSort         The sorting algorithm used for groups.
         * @param fileSortMethod    The sorting method used for files.
         */
        SearchCompleteEvent(Map<GroupKey, Integer> groupMap, List<FileSearchFiltering.FileFilter> searchfilters,
                FileSearch.AttributeType groupingAttribute, FileGroup.GroupSortingAlgorithm groupSort,
                FileSorter.SortingMethod fileSortMethod) {
            this.groupMap = groupMap;
            this.searchFilters = searchfilters;
            this.groupingAttribute = groupingAttribute;
            this.groupSort = groupSort;
            this.fileSortMethod = fileSortMethod;
        }

        /**
         * Get the map of groups found by the search.
         *
         * @return The map of groups which were found by the search.
         */
        Map<GroupKey, Integer> getGroupMap() {
            return Collections.unmodifiableMap(groupMap);
        }

        /**
         * Get the file filters used by the search.
         *
         * @return The search filters which were used by the search.
         */
        List<FileSearchFiltering.FileFilter> getFilters() {
            return Collections.unmodifiableList(searchFilters);
        }

        /**
         * Get the grouping attribute used by the search.
         *
         * @return The grouping attribute used by the search.
         */
        FileSearch.AttributeType getGroupingAttr() {
            return groupingAttribute;
        }

        /**
         * Get the sorting algorithm used for groups.
         *
         * @return The sorting algorithm used for groups.
         */
        FileGroup.GroupSortingAlgorithm getGroupSort() {
            return groupSort;
        }

        /**
         * Get the sorting method used for files.
         *
         * @return The sorting method used for files.
         */
        FileSorter.SortingMethod getFileSort() {
            return fileSortMethod;
        }

    }

    /**
     * Event to signal the completion of page retrieval and include the page
     * contents.
     */
    static final class PageRetrievedEvent {

        private final List<ResultFile> results;
        private final int page;
        private final FileType resultType;

        /**
         * Construct a new PageRetrievedEvent.
         *
         * @param resultType The type of files which exist in the page.
         * @param page       The number of the page which was retrieved.
         * @param results    The list of files in the page retrieved.
         */
        PageRetrievedEvent(FileType resultType, int page, List<ResultFile> results) {
            this.results = results;
            this.page = page;
            this.resultType = resultType;
        }

        /**
         * Get the list of files in the page retrieved.
         *
         * @return The list of files in the page retrieved.
         */
        List<ResultFile> getSearchResults() {
            return Collections.unmodifiableList(results);
        }

        /**
         * Get the page number which was retrieved.
         *
         * @return The number of the page which was retrieved.
         */
        int getPageNumber() {
            return page;
        }

        /**
         * Get the type of files which exist in the page.
         *
         * @return The type of files which exist in the page.
         */
        FileType getType() {
            return resultType;
        }
    }

    /**
     * Event to signal that there were no results for the search.
     */
    static final class NoResultsEvent {

        /**
         * Construct a new NoResultsEvent.
         */
        NoResultsEvent() {
            //no arg constructor
        }
    }

    /**
     * Event to signal that a search has been cancelled
     */
    static final class SearchCancelledEvent {

        /**
         * Construct a new SearchCancelledEvent.
         */
        SearchCancelledEvent() {
            //no arg constructor
        }

    }

    /**
     * Event to signal that a group has been selected.
     */
    static final class GroupSelectedEvent {

        private final FileType resultType;
        private final GroupKey groupKey;
        private final int groupSize;
        private final List<FileSearchFiltering.FileFilter> searchfilters;
        private final FileSearch.AttributeType groupingAttribute;
        private final FileGroup.GroupSortingAlgorithm groupSort;
        private final FileSorter.SortingMethod fileSortMethod;

        /**
         * Construct a new GroupSelectedEvent.
         *
         * @param searchfilters     The search filters which were used by the
         *                          search.
         * @param groupingAttribute The grouping attribute used by the search.
         * @param groupSort         The sorting algorithm used for groups.
         * @param fileSortMethod    The sorting method used for files.
         * @param groupKey          The key associated with the group which was
         *                          selected.
         * @param groupSize         The number of files in the group which was
         *                          selected.
         * @param resultType        The type of files which exist in the group.
         */
        GroupSelectedEvent(List<FileSearchFiltering.FileFilter> searchfilters,
                FileSearch.AttributeType groupingAttribute, FileGroup.GroupSortingAlgorithm groupSort,
                FileSorter.SortingMethod fileSortMethod, GroupKey groupKey, int groupSize, FileType resultType) {
            this.searchfilters = searchfilters;
            this.groupingAttribute = groupingAttribute;
            this.groupSort = groupSort;
            this.fileSortMethod = fileSortMethod;
            this.groupKey = groupKey;
            this.groupSize = groupSize;
            this.resultType = resultType;
        }

        /**
         * Get the type of files which exist in the group.
         *
         * @return The type of files which exist in the group.
         */
        FileType getResultType() {
            return resultType;
        }

        /**
         * Get the group key which is used to uniquely identify the group
         * selected.
         *
         * @return The group key which is used to uniquely identify the group
         *         selected.
         */
        GroupKey getGroupKey() {
            return groupKey;
        }

        /**
         * Get the number of files in the group which was selected.
         *
         * @return The number of files in the group which was selected.
         */
        int getGroupSize() {
            return groupSize;
        }

        /**
         * Get the sorting algorithm used in the group which was selected.
         *
         * @return The sorting algorithm used for groups.
         */
        FileGroup.GroupSortingAlgorithm getGroupSort() {
            return groupSort;
        }

        /**
         * Get the sorting method used for files in the group.
         *
         * @return The sorting method used for files.
         */
        FileSorter.SortingMethod getFileSort() {
            return fileSortMethod;
        }

        /**
         * Get the file filters which were used by the search
         *
         * @return The search filters which were used by the search.
         */
        List<FileSearchFiltering.FileFilter> getFilters() {
            return Collections.unmodifiableList(searchfilters);
        }

        /**
         * Get the grouping attribute used to create the groups.
         *
         * @return The grouping attribute used by the search.
         */
        FileSearch.AttributeType getGroupingAttr() {
            return groupingAttribute;
        }

    }

    /**
     * Event to signal that the visibility of the Details area should change.
     */
    static class DetailsVisibleEvent {

        private final boolean showDetailsArea;

        /**
         * Construct a new DetailsVisibleEvent.
         *
         * @param isVisible True if the details area should be visible, false
         *                  otherwise.
         */
        DetailsVisibleEvent(boolean isVisible) {
            showDetailsArea = isVisible;
        }

        /**
         * Get the visibility of the Details area.
         *
         * @return True if the details area should be visible, false otherwise.
         */
        boolean isShowDetailsArea() {
            return showDetailsArea;
        }
    }

}
