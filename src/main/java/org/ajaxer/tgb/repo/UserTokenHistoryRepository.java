package org.ajaxer.tgb.repo;

import org.ajaxer.tgb.constants.TokenDescription;
import org.ajaxer.tgb.entities.UserTokenHistory;
import org.ajaxer.tgb.entities.projection.UserDailyReferralProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Shakir Ansari
 * @since 2024-07-13
 */
@Repository
public interface UserTokenHistoryRepository extends ICreatorJPARepository<UserTokenHistory>
{
	@Query("SELECT e.createdBy as createdBy, DATE(e.createdOn) as createdOn, SUM(e.token) AS totalTokens " +
	       "FROM UserTokenHistory e " +
	       "WHERE e.tokenDescription IN :includedTokenDescriptions " +
	       "AND DATE(e.createdOn) = :inputDate " +
	       "AND (e.tokenDescription NOT IN :excludedTokenDescriptions OR DATE(e.createdOn) <> :excludedDate) " +
	       "AND e.createdBy.referredBy IS NOT NULL " +
	       "GROUP BY e.createdBy, DATE(e.createdOn) " +
	       "HAVING SUM(e.token) > 0 " +
	       "ORDER BY e.createdBy.id, DATE(e.createdOn)")
	Page<UserDailyReferralProjection> findTotalTokensByUserAndDate(
			@Param("includedTokenDescriptions") List<TokenDescription> includedTokenDescriptions,
			@Param("excludedTokenDescriptions") List<TokenDescription> excludedTokenDescriptions,
			@Param("inputDate") Date inputDate,
			@Param("excludedDate") Date excludedDate,
			Pageable pageable);
}
