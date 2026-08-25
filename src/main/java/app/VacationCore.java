package app;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.reflect.NonPrefixedBeanAccessor;
import org.apache.cayenne.runtime.CayenneRuntime;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The basics for accessing the DB through Cayenne.
 *
 * By default runs against an in-memory H2 database whose schema Cayenne creates on first use —
 * seed data is then loaded from the JSON files in woresources (see SeedData). Once we move to
 * actual-production, set -Dvacation.jdbcURL (plus username/password) to point at postgres instead.
 */

public class VacationCore {

	static {
		NonPrefixedBeanAccessor.register();
	}

	private static CayenneRuntime _runtime;
	private static ObjectContext _sharedContext;

	/**
	 * @return A newly constructed ObjectContext
	 */
	public static ObjectContext newContext() {
		return runtime().newContext();
	}

	/**
	 * @return A shared context for read-only work (queries from the page facades)
	 */
	public static ObjectContext sharedContext() {
		if( _sharedContext == null ) {
			_sharedContext = newContext();
		}

		return _sharedContext;
	}

	public static CayenneRuntime runtime() {
		if( _runtime == null ) {
			final DataNodeDescriptor.Builder dnd = DataNodeDescriptor.of( "node1" );

			final HikariConfig config = new HikariConfig();

			if( jdbcURL() != null ) {
				// If jdbcURL is set, use connection data from properties (the eventual postgres path)
				config.setJdbcUrl( jdbcURL() );
				config.setUsername( System.getProperty( "vacation.username" ) );
				config.setPassword( System.getProperty( "vacation.password" ) );
				config.setMaximumPoolSize( 4 );
			}
			else {
				// No jdbcURL set: create and use a new in-memory h2 DB.
				// We override the SchemaUpdateStrategyFactory rather than binding SchemaUpdateStrategy directly:
				// DefaultSchemaUpdateStrategyFactory reads the strategy from the DataNodeDescriptor and
				// instantiates it reflectively, never consulting a SchemaUpdateStrategy DI binding.
				dnd.createSchemaIfNeeded();

				config.setDriverClassName( "org.h2.Driver" );
				config.setJdbcUrl( "jdbc:h2:mem:vacation" );
			}

			dnd.dataSource( new HikariDataSource( config ) );

			_runtime = CayenneRuntime
					.of()
					.addConfig( "cayenne/cayenne-Vacation.xml" )
					.defaultDataNode( dnd.build() )
					.build();
		}

		return _runtime;
	}

	private static String jdbcURL() {
		return System.getProperty( "vacation.jdbcURL" );
	}
}
