package com.orientechnologies.orient.server.distributed;

import com.orientechnologies.orient.core.storage.impl.local.paginated.OLocalPaginatedStorage;
import com.orientechnologies.orient.core.storage.impl.memory.ODirectMemoryStorage;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.orientechnologies.orient.server.OServer;

/**
 * @author <a href="mailto:enisher@gmail.com">Artem Orobets</a>
 */
public class ODistributedStorageTest {
  @Test
  public void testSupportedFreezeTrue() {
    OLocalPaginatedStorage storage = Mockito.mock(OLocalPaginatedStorage.class);
    ODistributedStorage ds = new ODistributedStorage(Mockito.mock(OServer.class), storage);

    ds.freeze(true);

    Mockito.verify(storage).freeze(true);
  }

  @Test
  public void testSupportedFreezeFalse() {
    OLocalPaginatedStorage storage = Mockito.mock(OLocalPaginatedStorage.class);
    ODistributedStorage ds = new ODistributedStorage(Mockito.mock(OServer.class), storage);

    ds.freeze(false);

    Mockito.verify(storage).freeze(false);
  }

  @Test(expectedExceptions = { UnsupportedOperationException.class })
  public void testUnsupportedFreeze() {
    ODistributedStorage ds = new ODistributedStorage(Mockito.mock(OServer.class), Mockito.mock(ODirectMemoryStorage.class));

    ds.freeze(false);
  }

  @Test
  public void testSupportedRelease() {
    OLocalPaginatedStorage storage = Mockito.mock(OLocalPaginatedStorage.class);
    ODistributedStorage ds = new ODistributedStorage(Mockito.mock(OServer.class), storage);

    ds.release();

    Mockito.verify(storage).release();
  }

  @Test(expectedExceptions = { UnsupportedOperationException.class })
  public void testUnsupportedRelease() {
    ODistributedStorage ds = new ODistributedStorage(Mockito.mock(OServer.class), Mockito.mock(ODirectMemoryStorage.class));

    ds.release();
  }
}
