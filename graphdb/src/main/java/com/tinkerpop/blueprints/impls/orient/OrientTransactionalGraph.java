/*
 *
 *  *  Copyright 2014 Orient Technologies LTD (info(at)orientechnologies.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientechnologies.com
 *
 */

package com.tinkerpop.blueprints.impls.orient;

import org.apache.commons.configuration.Configuration;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OTransactionException;
import com.orientechnologies.orient.core.tx.OTransaction.TXSTATUS;
import com.orientechnologies.orient.core.tx.OTransactionNoTx;
import com.tinkerpop.blueprints.TransactionalGraph;

/**
 * A Blueprints implementation of the graph database OrientDB (http://www.orientechnologies.com)
 * 
 * @author Luca Garulli (http://www.orientechnologies.com)
 */
public abstract class OrientTransactionalGraph extends OrientBaseGraph implements TransactionalGraph {
  protected boolean useLog = true;

  /**
   * Constructs a new object using an existent database instance.
   *
   * @param iDatabase
   *          Underlying database object to attach
   */
  protected OrientTransactionalGraph(final ODatabaseDocumentTx iDatabase) {
    this(iDatabase, true, null, null);
  }

  protected OrientTransactionalGraph(final ODatabaseDocumentTx iDatabase, final String iUserName, final String iUserPasswd,
      final Settings iConfiguration) {
    super(iDatabase, iUserName, iUserPasswd, iConfiguration);
    makeActive();
    this.setAutoStartTx(settings.autoStartTx);

    if (settings.autoStartTx)
      begin();
  }

  protected OrientTransactionalGraph(final ODatabaseDocumentTx iDatabase, final boolean iAutoStartTx, final String iUserName,
      final String iUserPasswd) {
    super(iDatabase, iUserName, iUserPasswd, null);
    makeActive();
    this.setAutoStartTx(iAutoStartTx);

    if (iAutoStartTx)
      begin();
  }

  protected OrientTransactionalGraph(final OPartitionedDatabasePool pool) {
    super(pool);
    makeActive();

    begin();
  }

  protected OrientTransactionalGraph(final OPartitionedDatabasePool pool, final Settings configuration) {
    super(pool, configuration);
    makeActive();

    begin();
  }

  protected OrientTransactionalGraph(final String url) {
    this(url, true);
  }

  protected OrientTransactionalGraph(final String url, final boolean iAutoStartTx) {
    super(url, ADMIN, ADMIN);
    makeActive();
    setAutoStartTx(iAutoStartTx);

    if (iAutoStartTx)
      begin();
  }

  protected OrientTransactionalGraph(final String url, final String username, final String password) {
    this(url, username, password, true);
  }

  protected OrientTransactionalGraph(final String url, final String username, final String password, final boolean iAutoStartTx) {
    super(url, username, password);
    makeActive();
    this.setAutoStartTx(iAutoStartTx);

    if (iAutoStartTx)
      begin();
  }

  protected OrientTransactionalGraph(final Configuration configuration) {
    super(configuration);

    final Boolean autoStartTx = configuration.getBoolean("blueprints.orientdb.autoStartTx", null);
    if (autoStartTx != null)
      setAutoStartTx(autoStartTx);
  }

  public boolean isUseLog() {
    return useLog;
  }

  public OrientTransactionalGraph setUseLog(final boolean useLog) {
    this.useLog = useLog;
    return this;
  }

  /**
   * Closes a transaction.
   * 
   * @param conclusion
   *          Can be SUCCESS for commit and FAILURE to rollback.
   */
  @SuppressWarnings("deprecation")
  @Override
  public void stopTransaction(final Conclusion conclusion) {
    if (database.isClosed() || database.getTransaction() instanceof OTransactionNoTx
        || database.getTransaction().getStatus() != TXSTATUS.BEGUN)
      return;

    if (Conclusion.SUCCESS == conclusion)
      commit();
    else
      rollback();
  }

  /**
   * Commits the current active transaction.
   */
  public void commit() {
		makeActive();

    if (database == null)
      return;

    database.commit();
    if (settings.autoStartTx)
      begin();
  }

  /**
   * Rollbacks the current active transaction. All the pending changes are rollbacked.
   */
  public void rollback() {
		makeActive();

    if (database == null)
      return;

    database.rollback();
    if (settings.autoStartTx)
      begin();
  }

  public void begin() {
    final boolean txBegun = database.getTransaction().isActive();
    if (!txBegun) {
      database.begin();
      database.getTransaction().setUsingLog(useLog);
    }
  }

  @Override
  protected void autoStartTransaction() {
    final boolean txBegun = database.getTransaction().isActive();

    if (!settings.autoStartTx) {
      if (settings.requireTransaction && !txBegun)
        throw new OTransactionException("Transaction required to change the Graph");

      return;
    }

    if (!txBegun)
      begin();
  }

}
