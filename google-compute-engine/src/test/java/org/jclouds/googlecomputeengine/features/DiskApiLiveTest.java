/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.googlecomputeengine.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author David Alves
 */
public class DiskApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String DISK_NAME = "disk-api-live-test-disk";
   private static final int TIME_WAIT = 10;

   private URI zoneUrl;
   private int sizeGb = 1;

   private DiskApi api() {
      return api.getDiskApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertDisk() {
      Project project = api.getProjectApi().get(userProject.get());
      zoneUrl = getDefaultZoneUrl(project.getName());
      assertOperationDoneSucessfully(api().createInZone(DISK_NAME, sizeGb, zoneUrl), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertDisk")
   public void testGetDisk() {

      Disk disk = api().get(DISK_NAME);
      assertNotNull(disk);
      assertDiskEquals(disk);
   }

   @Test(groups = "live", dependsOnMethods = "testGetDisk")
   public void testListDisk() {

      PagedIterable<Disk> disks = api().list(new ListOptions.Builder()
              .filter("name eq " + DISK_NAME));

      List<Disk> disksAsList = Lists.newArrayList(disks.concat());

      assertEquals(disksAsList.size(), 1);

      assertDiskEquals(Iterables.getOnlyElement(disksAsList));

   }

   @Test(groups = "live", dependsOnMethods = "testListDisk")
   public void testDeleteDisk() {

      assertOperationDoneSucessfully(api().delete(DISK_NAME), TIME_WAIT);
   }

   private void assertDiskEquals(Disk result) {
      assertEquals(result.getName(), DISK_NAME);
      assertEquals(result.getSizeGb(), sizeGb);
      assertEquals(result.getZone(), zoneUrl);
   }

}
